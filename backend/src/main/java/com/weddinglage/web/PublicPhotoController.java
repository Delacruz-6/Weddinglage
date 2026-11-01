package com.weddinglage.web;

import com.weddinglage.service.PhotoService;
import com.weddinglage.web.dto.PhotoDtos;
import java.net.URL;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public/photos")
public class PublicPhotoController {

    private final PhotoService photoService;

    public PublicPhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping
    public PhotoDtos.PhotoUploadResponse upload(
            @RequestHeader(value = "X-Upload-Token", required = false) String uploadToken,
            @RequestPart("file") MultipartFile file) {
        return photoService.upload(uploadToken, file);
    }

    @GetMapping
    public List<PhotoDtos.PhotoPublicDto> list() {
        return photoService.listPublic(isAdmin());
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<Void> content(@PathVariable Long id) throws Exception {
        URL url = photoService.contentUrl(id, isAdmin());
        return ResponseEntity.status(HttpStatus.FOUND).location(url.toURI()).build();
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN"));
    }
}
