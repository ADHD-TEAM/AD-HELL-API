package com.adhd.ad_hell.domain.category.command.application.controller;

import com.adhd.ad_hell.domain.category.command.application.dto.request.CreateCategoryRequest;
import com.adhd.ad_hell.domain.category.command.application.dto.request.UpdateCategoryRequest;
import com.adhd.ad_hell.domain.category.command.application.service.CategoryCommandService;
import com.adhd.ad_hell.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Command", description = "카테고리 등록/수정/삭제 API")
public class CategoryCommandController {

  private final CategoryCommandService categoryCommandService;

  /* 기본 반환 전부 void 처리, 추후 변경 필요 */
  @Operation(
      summary = "카테고리 등록",
      description = "부모 카테고리 혹은 자식 카테고리를 등록한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "등록 성공"
      ),
  })
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createCategory(@RequestBody CreateCategoryRequest req) {
    categoryCommandService.createCategory(req);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(null));
  }

  @Operation(
      summary = "카테고리 수정",
      description = "카테고리 이름, 내용을 수정한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "수정 성공"
      ),
  })
  @PutMapping("/{categoryId}")
  public ResponseEntity<ApiResponse<Void>> updateCategory(@PathVariable Long categoryId, @RequestBody UpdateCategoryRequest req) {
    categoryCommandService.updateCategory(categoryId, req);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(
      summary = "카테고리 삭제",
      description = "부모 카테고리 혹은 자식 카테고리를 삭제한다. (부모 삭제 시 자식 전부 삭제 처리)"
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "삭제 성공"
      ),
  })
  @DeleteMapping("/{categoryId}")
  public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
    categoryCommandService.deleteCategory(categoryId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
