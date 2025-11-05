package com.adhd.ad_hell.domain.board.command.application.service;

import org.springframework.web.multipart.MultipartFile;



public interface FileStorage {
    /* 파일을 저장하고 저장된 파일명을 반환 */
    String store(MultipartFile file);
    /* 파일명을 기준으로 물리 파일을 삭제 */
    void delete(String fileName);
    /* 트랜잭션 보상 처리 */
    void deleteQuietly(String path);
}


