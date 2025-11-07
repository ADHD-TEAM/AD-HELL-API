package com.adhd.ad_hell.domain.category.command.application.service;

import com.adhd.ad_hell.domain.category.command.application.dto.request.CreateCategoryRequest;
import com.adhd.ad_hell.domain.category.command.application.dto.request.UpdateCategoryRequest;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.CategoryStatus;
import com.adhd.ad_hell.domain.category.command.domain.repository.CategoryRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryCommandServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryCommandService categoryCommandService;

  @Captor
  private ArgumentCaptor<Category> categoryCaptor;

  // ============================================
  //  createCategory() 테스트
  // ============================================
  @Nested
  @DisplayName("createCategory() 테스트")
  class CreateCategoryTests {

    @Test
    @DisplayName("부모 카테고리 없이 신규 카테고리 생성 성공")
    void createCategoryWithoutParent() {
      // given
      CreateCategoryRequest req = new CreateCategoryRequest(null, "음료", "시원한 음료");

      // when
      categoryCommandService.createCategory(req);

      // then
      verify(categoryRepository).save(categoryCaptor.capture());
      Category saved = categoryCaptor.getValue();

      assertThat(saved.getName()).isEqualTo("음료");
      assertThat(saved.getDescription()).isEqualTo("시원한 음료");
      assertThat(saved.getParent()).isNull();
      assertThat(saved.getStatus()).isEqualTo(CategoryStatus.ACTIVATE);
    }

    @Test
    @DisplayName("부모 카테고리가 존재하지 않으면 예외 발생")
    void createCategoryWithInvalidParent() {
      CreateCategoryRequest req = new CreateCategoryRequest(99L, "디저트", "달콤한 디저트");
      when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> categoryCommandService.createCategory(req))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("부모 카테고리와 함께 신규 카테고리 생성 성공")
    void createCategoryWithParent() {
      Category parent = Category.builder()
                                .name("음식")
                                .status(CategoryStatus.ACTIVATE)
                                .build();

      when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));

      CreateCategoryRequest req = new CreateCategoryRequest(1L, "한식", "한식 카테고리");

      categoryCommandService.createCategory(req);

      verify(categoryRepository).save(categoryCaptor.capture());
      Category saved = categoryCaptor.getValue();

      assertThat(saved.getParent()).isEqualTo(parent);
      assertThat(saved.getName()).isEqualTo("한식");
    }
  }

  // ============================================
  // updateCategory() 테스트
  // ============================================
  @Nested
  @DisplayName("updateCategory() 테스트")
  class UpdateCategoryTests {

    @Test
    @DisplayName("카테고리 정보 수정 성공")
    void updateCategorySuccess() {
      Category category = Category.builder()
                                  .name("음료")
                                  .description("시원한 음료")
                                  .status(CategoryStatus.ACTIVATE)
                                  .build();

      when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

      UpdateCategoryRequest req = new UpdateCategoryRequest("커피", "핫/아이스 커피");

      categoryCommandService.updateCategory(1L, req);

      assertThat(category.getName()).isEqualTo("커피");
      assertThat(category.getDescription()).isEqualTo("핫/아이스 커피");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 수정 시 예외 발생")
    void updateCategoryNotFound() {
      when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
      UpdateCategoryRequest req = new UpdateCategoryRequest("커피", "핫/아이스 커피");

      assertThatThrownBy(() -> categoryCommandService.updateCategory(1L, req))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }
  }

  // ============================================
  // deleteCategory() 테스트
  // ============================================
  @Nested
  @DisplayName("deleteCategory() 테스트")
  class DeleteCategoryTests {

    @Test
    @DisplayName("부모만 삭제 시 상태값 DELETE 로 변경")
    void deleteCategoryStatusChange() {
      Category category = Category.builder()
                                  .name("푸드")
                                  .status(CategoryStatus.ACTIVATE)
                                  .build();

      when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

      categoryCommandService.deleteCategory(1L);

      assertThat(category.getStatus()).isEqualTo(CategoryStatus.DELETE);
    }

    @Test
    @DisplayName("부모 삭제 시 자식 카테고리까지 상태값 DELETE 로 변경")
    void deleteParentAndChildren() {
      // given
      Category child1 = Category.builder()
                                .name("한식")
                                .status(CategoryStatus.ACTIVATE)
                                .build();
      Category child2 = Category.builder()
                                .name("양식")
                                .status(CategoryStatus.ACTIVATE)
                                .build();

      Category parent = Category.builder()
                                .name("푸드")
                                .status(CategoryStatus.ACTIVATE)
                                .build();

      parent.getChildren().addAll(List.of(child1, child2));

      when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));

      // when
      categoryCommandService.deleteCategory(1L);

      // then
      assertThat(parent.getStatus()).isEqualTo(CategoryStatus.DELETE);
      assertThat(child1.getStatus()).isEqualTo(CategoryStatus.DELETE);
      assertThat(child2.getStatus()).isEqualTo(CategoryStatus.DELETE);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 삭제 시 예외 발생")
    void deleteCategoryNotFound() {
      when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> categoryCommandService.deleteCategory(1L))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }
  }
}
