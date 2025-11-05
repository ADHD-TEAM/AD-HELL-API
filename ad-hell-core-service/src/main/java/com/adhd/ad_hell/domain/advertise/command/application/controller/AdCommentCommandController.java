//package com.adhd.ad_hell.domain.advertise.command.application.controller;
//
//import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCommentCreateRequest;
//import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCommentUpdateRequest;
//
//import com.adhd.ad_hell.common.dto.ApiResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//// ... existing code ...
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/comments")
//public class AdCommentCommandController {
//
//    private final AdCommentCommandService adCommentCommandService;
//
//    /* 광고 댓글 등록 */
//    @PostMapping
//    public ResponseEntity<ApiResponse<Void>> createAdComment(
//            @RequestBody AdCommentCreateRequest req
//    ) {
//        adCommentCommandService.createAdComment(req);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(ApiResponse.success(null));
//    }
//
//    /* 광고 댓글 수정 */
//    @PutMapping("/{adCommentId}")
//    public ResponseEntity<ApiResponse<Void>> updateAdComment(
//            @PathVariable Long adCommentId,
//            @RequestBody AdCommentUpdateRequest req
//    ) {
//        adCommentCommandService.updateAdComment(adCommentId, req);
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
//
//    /* 광고 댓글 삭제 */
//    @DeleteMapping("/{adCommentId}")
//    public ResponseEntity<ApiResponse<Void>> deleteAdComment(
//            @PathVariable Long adCommentId
//    ) {
//        adCommentCommandService.deleteAdComment(adCommentId);
//        return ResponseEntity.ok(ApiResponse.success(null));
//    }
//}
