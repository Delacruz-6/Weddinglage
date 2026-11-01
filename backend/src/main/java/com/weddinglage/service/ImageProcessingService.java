package com.weddinglage.service;

import com.weddinglage.web.error.ApiException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Set;
import javax.imageio.ImageIO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ImageProcessingService {

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp");

    public record ProcessedImage(byte[] bytes, String mimeType) {
    }

    public ProcessedImage process(byte[] original, String filenameHint) {
        String detected = detectMime(original, filenameHint);
        if (!ALLOWED.contains(detected)) {
            throw new ApiException("INVALID_MEDIA", "Tipo de archivo no permitido: " + detected, HttpStatus.BAD_REQUEST);
        }
        try {
            if ("image/webp".equals(detected)) {
                // Re-encode path for webp is limited without extra libs; store as-is without EXIF strip guarantee.
                return new ProcessedImage(original, detected);
            }
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(original));
            if (image == null) {
                throw new ApiException("INVALID_MEDIA", "No se pudo leer la imagen", HttpStatus.BAD_REQUEST);
            }
            String format = "image/png".equals(detected) ? "png" : "jpg";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Re-encoding via ImageIO drops EXIF/GPS metadata.
            if (!ImageIO.write(image, format, out)) {
                throw new ApiException("INVALID_MEDIA", "No se pudo procesar la imagen", HttpStatus.BAD_REQUEST);
            }
            String mime = "png".equals(format) ? "image/png" : "image/jpeg";
            return new ProcessedImage(out.toByteArray(), mime);
        } catch (ApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new ApiException("INVALID_MEDIA", "Error procesando la imagen", HttpStatus.BAD_REQUEST);
        }
    }

    private String detectMime(byte[] bytes, String filenameHint) {
        try {
            String fromStream = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
            if (fromStream != null) {
                return fromStream;
            }
        } catch (IOException ignored) {
            // fall through
        }
        if (filenameHint != null) {
            String lower = filenameHint.toLowerCase();
            if (lower.endsWith(".png")) {
                return "image/png";
            }
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                return "image/jpeg";
            }
            if (lower.endsWith(".webp")) {
                return "image/webp";
            }
        }
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8) {
            return "image/jpeg";
        }
        if (bytes.length >= 8 && bytes[0] == (byte) 0x89 && bytes[1] == 0x50) {
            return "image/png";
        }
        return "application/octet-stream";
    }
}
