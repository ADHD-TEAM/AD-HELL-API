package com.adhd.ad_hell.domain.board.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardCreateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardUpdateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.response.BoardCommandResponse;
import com.adhd.ad_hell.domain.board.command.application.service.BoardCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardCommandController {

    private final BoardCommandService service;

    /** 게시글 등록 */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<BoardCommandResponse>> create(
            @ModelAttribute BoardCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request, image)));
    }

    /** 게시글 수정 */
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<BoardCommandResponse>> update(
            @PathVariable Long id,
            @ModelAttribute BoardUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request, image)));
    }

    /** 게시글 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
