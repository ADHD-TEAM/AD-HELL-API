package com.adhd.ad_hell.common.storage;

import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorage {

    /** 실제 업로드 경로 */
    private final Path imageUploadDir;
    private final Path videoUploadDir;
    private final Path docUploadDir;

    public LocalFileStorageService(
            @Value("${file.image-dir}") String imageDir,
            @Value("${file.video-dir}") String videoDir,
            @Value("${file.doc-dir}") String docDir
    ) {
        this.imageUploadDir = initUploadDir(imageDir);
        this.videoUploadDir = initUploadDir(videoDir);
        this.docUploadDir   = initUploadDir(docDir);
    }

    private Path initUploadDir(String uploadDir) {
        Path dir = Paths.get(uploadDir).normalize().toAbsolutePath();

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("업로드 디렉터리 생성 실패 (path: {}): {}", dir, e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_DIR_CREATE_FAILED);
        }

        return dir;
    }

    /**
     * 공통 확장자 추출 로직
     */
    private String extractExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf('.') + 1))
                .map(String::toLowerCase)
                .orElse("");
    }

    /**
     * 확장자 기준으로 어떤 디렉토리에 저장할지 결정
     */
    private Path resolveBaseDirByExt(String ext) {
        FileExtensions type = FileExtensions.fromExtension(ext);
        if (type == null) {
            log.warn("허용되지 않은 확장자: {}", ext);
            throw new BusinessException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }

        return switch (type) {
            case IMAGE -> imageUploadDir;
            case VIDEO -> videoUploadDir;
            case DOC   -> docUploadDir;
        };
    }

    /**
     * 안전한 경로 확인
     */
    private Path safeResolve(Path baseDir, String fileName) {
        Path p = baseDir.resolve(fileName).normalize().toAbsolutePath();
        if (!p.startsWith(baseDir)) {
            throw new BusinessException(ErrorCode.FILE_PATH_TRAVERSAL_DETECTED);
        }
        return p;
    }

    /**
     * 파일 저장
     * @return FileStorageResult (저장된 이름 + 절대경로)
     */
    @Override
    public FileStorageResult store(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BusinessException(ErrorCode.FILE_EMPTY);

        final String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank())
            throw new BusinessException(ErrorCode.FILE_NAME_NOT_PRESENT);

        final String ext = extractExtension(originalName);
        if (ext.isEmpty()) {
            log.warn("확장자를 찾을 수 없음: {}", originalName);
            throw new BusinessException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }

        // 확장자 → Enum 타입 → 디렉토리 결정
        Path baseDir = resolveBaseDirByExt(ext);

        final String storedName = UUID.randomUUID() + "." + ext;
        final Path target = safeResolve(baseDir, storedName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.error("파일 저장 실패 [{}]: {}", storedName, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.FILE_SAVE_IO_ERROR);
        }

        return new FileStorageResult(storedName, target.toString());
    }

    /**
     * 파일 삭제
     */
    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) return;

        final String ext = extractExtension(fileName);
        if (ext.isEmpty()) {
            log.warn("확장자를 찾을 수 없어 삭제 불가: {}", fileName);
            throw new BusinessException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }

        Path baseDir = resolveBaseDirByExt(ext);
        final Path path = safeResolve(baseDir, fileName);

        try {
            if (!Files.deleteIfExists(path)) {
                log.warn("삭제할 파일이 존재하지 않음: {}", path);
            }
        } catch (IOException ex) {
            log.error("파일 삭제 실패 [{}]: {}", fileName, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.FILE_DELETE_IO_ERROR);
        }
    }

    /**
     * 조용히 삭제 (예외 무시)
     */
    @Override
    public void deleteQuietly(String fileName) {
        try {
            delete(fileName);
        } catch (Exception ignore) {
            log.debug("파일 삭제 무시됨: {}", fileName);
        }
    }
}
