package com.adhd.ad_hell.domain.category.query.service;

import com.adhd.ad_hell.domain.category.command.domain.aggregate.CategoryStatus;
import com.adhd.ad_hell.domain.category.query.dto.response.CategoryDetailResponse;
import com.adhd.ad_hell.domain.category.query.dto.response.CategoryTreeResponse;
import com.adhd.ad_hell.domain.category.query.mapper.CategoryMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryQueryServiceTest {

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryQueryService categoryQueryService;

  // ========================================
  // getCategoryDetail() 테스트
  // ========================================
  @Nested
  @DisplayName("getCategoryDetail() 테스트")
  class GetCategoryDetailTests {

    @Test
    @DisplayName("카테고리 상세 조회 성공")
    void getCategoryDetail_success() {
      // given
      Long categoryId = 1L;
      CategoryDetailResponse response = new CategoryDetailResponse(
          1L, "음료", "시원한 음료", CategoryStatus.ACTIVATE, null, null
      );

      when(categoryMapper.findCategoryById(categoryId)).thenReturn(response);

      // when
      CategoryDetailResponse result = categoryQueryService.getCategoryDetail(categoryId);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getName()).isEqualTo("음료");
      verify(categoryMapper).findCategoryById(categoryId);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID 조회 시 예외 발생")
    void getCategoryDetail_notFound() {
      // given
      Long categoryId = 99L;
      when(categoryMapper.findCategoryById(categoryId)).thenReturn(null);

      // when & then
      assertThatThrownBy(() -> categoryQueryService.getCategoryDetail(categoryId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage());

      verify(categoryMapper).findCategoryById(categoryId);
    }
  }

  // ========================================
  // getCategoryTree() 테스트
  // ========================================
  @Nested
  @DisplayName("getCategoryTree() 테스트")
  class GetCategoryTreeTests {

    @Test
    @DisplayName("루트-자식 계층 구조로 트리 생성 성공")
    void getCategoryTree_success() {
      // given
      CategoryTreeResponse root = new CategoryTreeResponse(1L, "푸드", "설명", CategoryStatus.ACTIVATE, null, new ArrayList<>());
      CategoryTreeResponse child1 = new CategoryTreeResponse(2L, "한식", "설명", CategoryStatus.ACTIVATE, 1L, new ArrayList<>());
      CategoryTreeResponse child2 = new CategoryTreeResponse(3L, "양식", "설명",  CategoryStatus.ACTIVATE, 1L, new ArrayList<>());
      CategoryTreeResponse grandChild = new CategoryTreeResponse(4L,  "찌개류", "설명", CategoryStatus.ACTIVATE, 2L, new ArrayList<>());
      CategoryTreeResponse orphan = new CategoryTreeResponse(5L, "잘못된 노드", "설명", CategoryStatus.ACTIVATE, 99L, new ArrayList<>());

      when(categoryMapper.findAllCategories(null))
          .thenReturn(List.of(root, child1, child2, grandChild, orphan));

      // when
      List<CategoryTreeResponse> result = categoryQueryService.getCategoryTree(null);

      // then
      assertThat(result).hasSize(1);
      CategoryTreeResponse rootNode = result.get(0);
      assertThat(rootNode.getChildren()).hasSize(2);
      assertThat(rootNode.getChildren().get(0).getChildren()).hasSize(1);
      assertThat(result).extracting("id").doesNotContain(5L);
    }

    @Test
    @DisplayName("자식이 없는 카테고리들만 있을 때 루트 리스트 반환")
    void getCategoryTree_noChildren() {
      CategoryTreeResponse a = new CategoryTreeResponse(1L, "A", "detail", CategoryStatus.ACTIVATE, null, List.of());
      CategoryTreeResponse b = new CategoryTreeResponse(2L, "B", "detail", CategoryStatus.ACTIVATE, null, List.of());

      when(categoryMapper.findAllCategories(null)).thenReturn(List.of(a, b));

      List<CategoryTreeResponse> result = categoryQueryService.getCategoryTree(null);

      assertThat(result).hasSize(2);
      assertThat(result).extracting("name").containsExactlyInAnyOrder("A", "B");
    }
  }
}
