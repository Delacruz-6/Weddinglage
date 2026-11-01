package com.weddinglage.service;

import com.weddinglage.config.WeddinglageProperties;
import com.weddinglage.domain.Photo;
import com.weddinglage.domain.PhotoPublicationConfig;
import com.weddinglage.domain.PhotoUploadToken;
import com.weddinglage.repository.PhotoPublicationConfigRepository;
import com.weddinglage.repository.PhotoRepository;
import com.weddinglage.repository.PhotoUploadTokenRepository;
import com.weddinglage.util.TokenHasher;
import com.weddinglage.web.dto.PhotoDtos;
import com.weddinglage.web.error.ApiException;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoUploadTokenRepository uploadTokenRepository;
    private final PhotoPublicationConfigRepository publicationConfigRepository;
    private final ImageProcessingService imageProcessingService;
    private final AuditService auditService;
    private final WeddinglageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public PhotoService(
            PhotoRepository photoRepository,
            PhotoUploadTokenRepository uploadTokenRepository,
            PhotoPublicationConfigRepository publicationConfigRepository,
            ImageProcessingService imageProcessingService,
            AuditService auditService,
            WeddinglageProperties properties,
            S3Client s3Client,
            S3Presigner s3Presigner) {
        this.photoRepository = photoRepository;
        this.uploadTokenRepository = uploadTokenRepository;
        this.publicationConfigRepository = publicationConfigRepository;
        this.imageProcessingService = imageProcessingService;
        this.auditService = auditService;
        this.properties = properties;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Transactional
    public PhotoDtos.PhotoUploadResponse upload(String rawUploadToken, MultipartFile file) {
        validateUploadToken(rawUploadToken);
        try {
            var processed = imageProcessingService.process(file.getBytes(), file.getOriginalFilename());
            ensureBucket();
            String key = "photos/" + UUID.randomUUID();
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.getMinio().getBucket())
                            .key(key)
                            .contentType(processed.mimeType())
                            .build(),
                    RequestBody.fromBytes(processed.bytes()));

            Photo photo = new Photo();
            photo.setStorageKey(key);
            photo.setOriginalName(file.getOriginalFilename());
            photo.setMimeType(processed.mimeType());
            photo.setStatus(Photo.STATUS_PENDING);
            photoRepository.save(photo);
            return new PhotoDtos.PhotoUploadResponse(photo.getId(), photo.getStatus());
        } catch (IOException ex) {
            throw new ApiException("INVALID_MEDIA", "No se pudo leer el archivo", HttpStatus.BAD_REQUEST);
        } catch (S3Exception ex) {
            throw new ApiException("STORAGE_ERROR", "Error al guardar la foto", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Transactional(readOnly = true)
    public List<PhotoDtos.PhotoPublicDto> listPublic(boolean adminBypass) {
        if (!adminBypass && !isGalleryPublished()) {
            return List.of();
        }
        return photoRepository.findByStatusOrderByUploadedAtDesc(Photo.STATUS_APPROVED).stream()
                .map(p -> new PhotoDtos.PhotoPublicDto(p.getId(), p.getMimeType(), p.getUploadedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public URL contentUrl(Long id, boolean adminBypass) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Foto no encontrada", HttpStatus.NOT_FOUND));
        if (!adminBypass) {
            if (!isGalleryPublished() || !Photo.STATUS_APPROVED.equals(photo.getStatus())) {
                throw new ApiException("NOT_PUBLISHED", "La galería aún no está publicada", HttpStatus.FORBIDDEN);
            }
        }
        var getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                .bucket(properties.getMinio().getBucket())
                .key(photo.getStorageKey())
                .build();
        return s3Presigner.presignGetObject(GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .getObjectRequest(getObjectRequest)
                        .build())
                .url();
    }

    @Transactional(readOnly = true)
    public List<PhotoDtos.PhotoAdminDto> listAdmin() {
        return photoRepository.findAll().stream().map(this::toAdminDto).toList();
    }

    @Transactional
    public PhotoDtos.PhotoAdminDto patch(Long id, PhotoDtos.PhotoPatchRequest request, String actor) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Foto no encontrada", HttpStatus.NOT_FOUND));
        String status = request.status() == null ? null : request.status().toUpperCase();
        if (status == null || !(Photo.STATUS_APPROVED.equals(status)
                || Photo.STATUS_REJECTED.equals(status)
                || Photo.STATUS_PENDING.equals(status))) {
            throw new ApiException("VALIDATION_ERROR", "Estado no válido", HttpStatus.BAD_REQUEST);
        }
        photo.setStatus(status);
        photo.setReviewedAt(Instant.now());
        photo.setReviewedBy(actor);
        photoRepository.save(photo);
        if (Photo.STATUS_APPROVED.equals(status)) {
            auditService.record(actor, "PHOTO_APPROVE", "Photo", String.valueOf(id), null);
        }
        return toAdminDto(photo);
    }

    @Transactional
    public void delete(Long id, String actor) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Foto no encontrada", HttpStatus.NOT_FOUND));
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.getMinio().getBucket())
                    .key(photo.getStorageKey())
                    .build());
        } catch (S3Exception ignored) {
            // still delete DB row
        }
        photoRepository.delete(photo);
        auditService.record(actor, "PHOTO_DELETE", "Photo", String.valueOf(id), null);
    }

    @Transactional
    public PhotoDtos.UploadTokenResponse createUploadToken(PhotoDtos.UploadTokenRequest request) {
        String raw = TokenHasher.generateToken(24);
        PhotoUploadToken token = new PhotoUploadToken();
        token.setTokenHash(TokenHasher.hash(raw, properties.getTokenPepper()));
        token.setLabel(request.label());
        token.setExpiresAt(request.expiresAt());
        token.setRevoked(false);
        uploadTokenRepository.save(token);
        return new PhotoDtos.UploadTokenResponse(token.getId(), raw, token.getExpiresAt());
    }

    @Transactional(readOnly = true)
    public PhotoDtos.PublicationConfigDto getPublicationConfig() {
        return toPublicationDto(requirePublication());
    }

    @Transactional
    public PhotoDtos.PublicationConfigDto updatePublicationConfig(
            PhotoDtos.PublicationConfigUpdateRequest request, String actor) {
        PhotoPublicationConfig config = requirePublication();
        if (request.publishAt() != null) {
            config.setPublishAt(request.publishAt());
        }
        if (request.adminAuthorized() != null) {
            config.setAdminAuthorized(request.adminAuthorized());
            if (Boolean.TRUE.equals(request.adminAuthorized())) {
                config.setAuthorizedBy(actor);
                config.setAuthorizedAt(Instant.now());
                auditService.record(actor, "PUBLICATION_AUTHORIZE", "PhotoPublicationConfig", "1", null);
            } else {
                config.setAuthorizedBy(null);
                config.setAuthorizedAt(null);
            }
        }
        config.setUpdatedAt(Instant.now());
        publicationConfigRepository.save(config);
        return toPublicationDto(config);
    }

    public boolean isGalleryPublished() {
        return PublicationPolicy.isPublished(requirePublication(), Instant.now());
    }

    private void validateUploadToken(String rawUploadToken) {
        if (rawUploadToken == null || rawUploadToken.isBlank()) {
            throw new ApiException("UPLOAD_DENIED", "Token de subida requerido", HttpStatus.UNAUTHORIZED);
        }
        String hash = TokenHasher.hash(rawUploadToken, properties.getTokenPepper());
        PhotoUploadToken token = uploadTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new ApiException("UPLOAD_DENIED", "Token de subida no válido", HttpStatus.UNAUTHORIZED));
        if (token.isRevoked()) {
            throw new ApiException("UPLOAD_DENIED", "Token de subida revocado", HttpStatus.UNAUTHORIZED);
        }
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("UPLOAD_DENIED", "Token de subida caducado", HttpStatus.UNAUTHORIZED);
        }
    }

    private void ensureBucket() {
        String bucket = properties.getMinio().getBucket();
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException ex) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            }
        }
    }

    private PhotoPublicationConfig requirePublication() {
        return publicationConfigRepository.findById((short) 1)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Configuración de publicación no encontrada", HttpStatus.NOT_FOUND));
    }

    private PhotoDtos.PhotoAdminDto toAdminDto(Photo photo) {
        return new PhotoDtos.PhotoAdminDto(
                photo.getId(),
                photo.getOriginalName(),
                photo.getMimeType(),
                photo.getStatus(),
                photo.getUploadedAt(),
                photo.getReviewedAt(),
                photo.getReviewedBy());
    }

    private PhotoDtos.PublicationConfigDto toPublicationDto(PhotoPublicationConfig config) {
        return new PhotoDtos.PublicationConfigDto(
                config.getPublishAt(),
                config.isAdminAuthorized(),
                config.getAuthorizedBy(),
                config.getAuthorizedAt());
    }
}
