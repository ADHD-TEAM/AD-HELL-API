package com.adhd.ad_hell.domain.category.query.controller;

import com.adhd.ad_hell.domain.category.query.dto.response.CategoryDetailResponse;
import com.adhd.ad_hell.domain.category.query.dto.response.CategoryTreeResponse;
import com.adhd.ad_hell.domain.category.query.service.CategoryQueryService;
import com.adhd.ad_hell.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "Category Query", description = "카테고리 조회 API")
public class CategoryQueryController {

  private final CategoryQueryService categoryQueryService;

  @Operation(
      summary = "카테고리 상세 조회",
      description = "특정 카테고리의 상세 내역을 확인한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping("/{categoryId}")
  public ResponseEntity<ApiResponse<CategoryDetailResponse>> getCategoryDetails(
      @PathVariable Long categoryId
  ) {
    CategoryDetailResponse response = categoryQueryService.getCategoryDetail(categoryId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(
      summary = "카테고리 조회",
      description = "카테고리의 전체 내역을 확인한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getCategories(
      @RequestParam(required = false) String keyword
  ) {
    List<CategoryTreeResponse> tree = categoryQueryService.getCategoryTree(keyword);
    return ResponseEntity.ok(ApiResponse.success(tree));
  }
}
