package com.adhd.ad_hell.common.storage;

import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/* 로컬 파일 시스템에 이미지를 저장하고 삭제하는 기능을 담당
 * FileStorage 인터페이스의 구현체이며, 나중에 S3 등 다른 저장소로 교체 가능
 * 파일 업로드 시 확장자 검사와 경로 안전성 검증을 수행
 */
@Service
@Slf4j
public class LocalFileStorageService implements FileStorage {

    /* 허용되는 이미지 확장자 목록 (화이트리스트) */
    private static final Set<String> ALLOWED_EXT = Set.of(
            // 이미지 파일
            "png", "jpg", "jpeg", "gif",

            // 동영상 파일
            "mp4", "mov", "avi", "mkv", "wmv", "flv", "webm", "m4v", "ts", "mpeg", "mpg",

            // 서류(문서) 파일
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "hwp", "txt", "csv", "rtf", "odt", "md"	);

    /* 실제 업로드될 디렉터리 경로 */
    private final Path uploadDir;

    /* 설정에서 업로드 경로를 받아와 Path 객체로 변환하고 해당 디렉터리가 없으면 생성 */
    public LocalFileStorageService(@Value("${image.image-dir}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).normalize().toAbsolutePath();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            log.error("업로드 디렉터리 생성 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_DIR_CREATE_FAILED);
        }
    }

    /* 파일 저장 메서드
     * MultipartFile에서 확장자를 추출하고 화이트리스트 검증을 통과한 경우
     * 랜덤한 파일명을 생성하여 지정된 디렉터리에 파일을 저장 후 저장된 파일명을 반환
     */
    @Override
    public String store(MultipartFile file) {
        // 1. 파일 유효성 검증
        if (file == null || file.isEmpty()) throw new BusinessException(ErrorCode.FILE_EMPTY);

        // 2. 확장자 추출 및 검증
        final String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new BusinessException(ErrorCode.FILE_NAME_NOT_PRESENT);
        }
        final String ext = Optional.of(originalName)
                .filter(name -> name.contains("."))                   // 점(.)이 있는 경우만
                .map(name -> name.substring(name.lastIndexOf('.') + 1)) // 마지막 점 이후 부분 추출
                .map(String::toLowerCase)                             // 소문자로 변환
                .orElse("");                                          // 확장자가 없는 경우 빈 문자열
        if (!ALLOWED_EXT.contains(ext)) {
            log.warn("허용되지 않은 확장자: {}", ext);
            throw new BusinessException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }

        // 3. 파일명 랜덤화 (UUID)
        final String fileName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        // 4. 안전한 파일 경로 생성
        final Path target = safeResolve(fileName);

        // 5. 파일 저장
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.error("파일 저장 실패 [{}]: {}", fileName, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.FILE_SAVE_IO_ERROR);
        }

        return fileName;
    }

    /* 파일 삭제 메서드
     * 파일명을 기반으로 실제 경로를 계산하고 해당 경로의 파일을 삭제
     * 파일이 존재하지 않는 경우 경고 로그만 출력
     */
    @Override
    public void delete(String fileName) {
        final Path path = safeResolve(fileName);
        try {
            if (!Files.deleteIfExists(path)) {
                log.warn("삭제할 파일이 존재하지 않음: {}", path);
            }
        } catch (IOException ex) {
            log.error("파일 삭제 실패 [{}]: {}", fileName, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.FILE_DELETE_IO_ERROR);
        }
    }

    /* 예외를 삼키고 조용히 삭제하는 메서드
     * 트랜잭션 롤백 시 보상 처리에서 사용하며 실패하더라도 애플리케이션 로직에 영향을 주지 않음
     */
    @Override
    public void deleteQuietly(String fileName) {
        try {
            delete(fileName);
        } catch (Exception ignore) { }
    }

    /* 안전한 파일 경로 생성 메서드
     * 경로 이동 공격(Path Traversal)을 방지하기 위해 업로드 디렉터리 경로를 기준으로 normalize 후 검증
     * 업로드 경로 밖으로 벗어나는 경로는 예외 처리
     */
    private Path safeResolve(String fileName) {
        Path p = this.uploadDir.resolve(fileName).normalize().toAbsolutePath();
        if (!p.startsWith(this.uploadDir)) {
            throw new BusinessException(ErrorCode.FILE_PATH_TRAVERSAL_DETECTED);
        }
        return p;
    }
}