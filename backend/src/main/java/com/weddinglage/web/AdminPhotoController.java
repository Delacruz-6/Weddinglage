package com.weddinglage.web;

import com.weddinglage.service.PhotoService;
import com.weddinglage.web.dto.PhotoDtos;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminPhotoController {

    private final PhotoService photoService;

    public AdminPhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/photos")
    public List<PhotoDtos.PhotoAdminDto> list() {
        return photoService.listAdmin();
    }

    @PatchMapping("/photos/{id}")
    public PhotoDtos.PhotoAdminDto patch(
            @PathVariable Long id, @RequestBody PhotoDtos.PhotoPatchRequest request, Authentication auth) {
        return photoService.patch(id, request, auth.getName());
    }

    @DeleteMapping("/photos/{id}")
    public Map<String, Boolean> delete(@PathVariable Long id, Authentication auth) {
        photoService.delete(id, auth.getName());
        return Map.of("ok", true);
    }

    @PostMapping("/photos/upload-token")
    public PhotoDtos.UploadTokenResponse createToken(@RequestBody PhotoDtos.UploadTokenRequest request) {
        return photoService.createUploadToken(request);
    }

    @GetMapping("/publication")
    public PhotoDtos.PublicationConfigDto getPublication() {
        return photoService.getPublicationConfig();
    }

    @PutMapping("/publication")
    public PhotoDtos.PublicationConfigDto putPublication(
            @RequestBody PhotoDtos.PublicationConfigUpdateRequest request, Authentication auth) {
        return photoService.updatePublicationConfig(request, auth.getName());
    }
}
