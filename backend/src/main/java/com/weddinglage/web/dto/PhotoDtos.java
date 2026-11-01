package com.weddinglage.web.dto;

import java.time.Instant;

public final class PhotoDtos {

    private PhotoDtos() {
    }

    public record PhotoPublicDto(Long id, String mimeType, Instant uploadedAt) {
    }

    public record PhotoAdminDto(
            Long id,
            String originalName,
            String mimeType,
            String status,
            Instant uploadedAt,
            Instant reviewedAt,
            String reviewedBy
    ) {
    }

    public record PhotoPatchRequest(String status) {
    }

    public record PhotoUploadResponse(Long id, String status) {
    }

    public record UploadTokenRequest(String label, Instant expiresAt) {
    }

    public record UploadTokenResponse(Long id, String token, Instant expiresAt) {
    }

    public record PublicationConfigDto(
            Instant publishAt,
            boolean adminAuthorized,
            String authorizedBy,
            Instant authorizedAt
    ) {
    }

    public record PublicationConfigUpdateRequest(Instant publishAt, Boolean adminAuthorized) {
    }
}
