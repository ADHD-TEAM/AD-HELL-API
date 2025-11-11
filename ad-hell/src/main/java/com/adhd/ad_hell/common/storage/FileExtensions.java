package com.adhd.ad_hell.common.storage;

import java.util.Collections;
import java.util.Set;

/**
 * 파일 확장자 타입을 분류하는 Enum 클래스.
 *
 * IMAGE, VIDEO, DOC 세 가지 그룹으로 구분되며,
 * 특정 확장자가 어느 그룹에 속하는지 판단하는 기능을 제공합니다.
 */
public enum FileExtensions {

    /* ----------------------------- ENUM 정의부 ----------------------------- */

    // 이미지 파일 확장자 그룹
    IMAGE(Set.of(
            "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg", "heic", "heif", "avif", "tiff"
    )),

    // 비디오(동영상) 파일 확장자 그룹
    VIDEO(Set.of(
            "mp4", "mov", "avi", "mkv", "wmv", "flv", "webm", "m4v", "ts", "mpeg", "mpg", "3gp"
    )),

    // 문서 파일 확장자 그룹
    DOC(Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "hwp", "txt", "csv", "rtf", "odt", "md"
    ));

    /* ----------------------------- 필드 정의부 ----------------------------- */

    // 각 타입별로 허용되는 확장자 목록
    private final Set<String> extensions;

    /* ----------------------------- 생성자 ----------------------------- */

    /**
     * Enum 생성자
     * @param extensions 해당 파일 그룹의 확장자 목록
     */
    FileExtensions(Set<String> extensions) {
        this.extensions = extensions;
    }

    /* ----------------------------- Getter ----------------------------- */

    /**
     * 현재 Enum 인스턴스의 확장자 목록을 읽기 전용(Set.unmodifiableSet)으로 반환
     */
    public Set<String> getExtensions() {
        return Collections.unmodifiableSet(extensions);
    }

    /* ----------------------------- 메서드 정의부 ----------------------------- */

    /**
     * 전달받은 확장자가 현재 그룹(IMAGE, VIDEO, DOC)에 포함되어 있는지 확인
     *
     * @param ext 검사할 확장자 (예: "jpg", "mp4")
     * @return 포함되어 있으면 true, 아니면 false
     * return 에서 사용하는 contains 는 set 메서드의 contains 재귀 호출x
     */
    public boolean contains(String ext) {
        if (ext == null || ext.isBlank()) return false;
        return extensions.contains(ext.toLowerCase());
    }

    /* ----------------------------- 정적 헬퍼 메서드 ----------------------------- */

    /**
     * 이미지 확장자인지 여부를 확인
     */
    public static boolean isImageExt(String ext) {
        return IMAGE.contains(ext);
    }

    /**
     * 비디오(동영상) 확장자인지 여부를 확인
     */
    public static boolean isVideoExt(String ext) {
        return VIDEO.contains(ext);
    }

    /**
     * 문서 확장자인지 여부를 확인
     */
    public static boolean isDocExt(String ext) {
        return DOC.contains(ext);
    }

    /**
     * 전달된 확장자를 기반으로 Enum 타입을 반환
     * (예: "jpg" → IMAGE, "mp4" → VIDEO, "pdf" → DOC)
     *
     * @param ext 확장자 문자열
     * @return 해당 확장자에 대응하는 FileExtensions Enum, 없으면 null
     */
    public static FileExtensions fromExtension(String ext) {
        if (isImageExt(ext)) return IMAGE;
        if (isVideoExt(ext)) return VIDEO;
        if (isDocExt(ext)) return DOC;
        return null;
    }
}
