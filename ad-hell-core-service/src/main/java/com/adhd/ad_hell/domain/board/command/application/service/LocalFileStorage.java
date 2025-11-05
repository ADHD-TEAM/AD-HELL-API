package com.adhd.ad_hell.domain.board.command.application.service;

import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage{

    @Value("${image.upload-dir:/tmp/uploads}")
    private String uploadDir;

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY); // 혹은 null 리턴도 가능
        }
        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "unnamed");
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadDir);
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
            return target.toString().replace('\\', '/');
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_STORE_FAILED, e);
        }
    }

    @Override
    public void delete(String fileName) {

    }

    public void deleteQuietly(String path) {
        if (path == null) return;
        try { Files.deleteIfExists(Paths.get(path)); } catch (Exception ignored) {}
    }
}