package com.adhd.ad_hell.domain.board.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardCreateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardUpdateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.response.BoardCommandResponse;
import com.adhd.ad_hell.domain.board.command.application.service.BoardCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Board Command", description = "게시글 관리 API")
public class BoardCommandController {

    private final BoardCommandService boardCommandService;

    /* -------------------- 게시글 등록 -------------------- */
    @Operation(
            summary = "게시글 등록 (다중 이미지 지원)",
            description = "boardInfo(JSON) + imageFiles(파일 배열) 또는 image(단일 파일)을 업로드하여 새 게시글을 생성한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "게시글 등록 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "카테고리 또는 사용자 정보를 찾을 수 없음"
            ),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardCommandResponse> createBoard(
            @RequestPart("boardInfo") @Valid BoardCreateRequest req,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {

        BoardCommandResponse resp = boardCommandService.createBoard(req, imageFiles);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /* -------------------- 게시글 수정 -------------------- */
    @Operation(
            summary = "게시글 수정",
            description = "게시글의 제목, 내용, 상태, 카테고리 정보를 수정한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시글 수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글 또는 카테고리를 찾을 수 없음"
            ),
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable Long boardId,
            @RequestBody @Valid BoardUpdateRequest req
    ) {
        boardCommandService.updateBoard(boardId, req);
        return ResponseEntity.ok().build();
    }

    /* -------------------- 게시글 삭제 -------------------- */
    @Operation(
            summary = "게시글 삭제",
            description = "게시글 및 연관된 파일을 모두 삭제한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "게시글 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            ),
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardCommandService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    /* -------------------- 이미지 추가 -------------------- */
    @Operation(
            summary = "게시글에 이미지 추가 업로드",
            description = "기존 게시글에 새로운 이미지 파일을 추가한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "이미지 추가 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            ),
    })
    @PostMapping(value = "/{boardId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Integer>> appendImagesBoard(
            @PathVariable Long boardId,
            @RequestPart("imageFiles") List<MultipartFile> imageFiles
    ) {
        int added = boardCommandService.appendImagesBoard(boardId, imageFiles);
        return ResponseEntity.ok(ApiResponse.success(added));
    }

    /* -------------------- 이미지 삭제 -------------------- */
    @Operation(
            summary = "게시글 이미지 1건 삭제",
            description = "storedName 기준으로 게시글의 이미지를 삭제한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "이미지 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글 또는 파일을 찾을 수 없음"
            ),
    })
    @DeleteMapping("/{boardId}/images/{storedName}")
    public ResponseEntity<ApiResponse<Void>> removeOneImageBoard(
            @PathVariable Long boardId,
            @PathVariable String storedName
    ) {
        boardCommandService.removeOneImageBoard(boardId, storedName);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
