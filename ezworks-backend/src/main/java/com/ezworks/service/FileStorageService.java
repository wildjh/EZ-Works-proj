package com.ezworks.service;

import com.ezworks.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private final Path uploadRoot;

    public FileStorageService(@Value("${ezworks.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de uploads", e);
        }
    }

    public String storeImage(MultipartFile file, String subdirectory) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !IMAGE_TYPES.contains(contentType)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Solo se permiten imágenes JPG, PNG, WEBP o GIF");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La imagen no puede superar 5 MB");
        }

        String extension = extensionFor(contentType);
        String filename = UUID.randomUUID() + extension;

        try {
            Path targetDir = uploadRoot.resolve(subdirectory).normalize();
            if (!targetDir.startsWith(uploadRoot)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Ruta de almacenamiento inválida");
            }
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subdirectory + "/" + filename;
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el archivo");
        }
    }

    public void deleteIfExists(String publicUrl) {
        if (!StringUtils.hasText(publicUrl) || !publicUrl.startsWith("/uploads/")) {
            return;
        }
        Path filePath = uploadRoot.resolve(publicUrl.substring("/uploads/".length())).normalize();
        if (!filePath.startsWith(uploadRoot)) {
            return;
        }
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // best effort
        }
    }

    private static String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
