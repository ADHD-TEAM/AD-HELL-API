package com.adhd.ad_hell.domain.category.query.service.provider;

import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.query.mapper.CategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryProviderImplTest {

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryProviderImpl categoryProvider;

  @Test
  @DisplayName("Category ID로 조회 성공 시 Category 엔티티 반환")
  void getCategoryEntityById_success() {
    // given
    Long categoryId = 1L;
    Category mockCategory = Category.builder()
                                    .name("음료")
                                    .description("시원한 음료")
                                    .build();

    when(categoryMapper.getCategoryEntityById(categoryId)).thenReturn(mockCategory);

    // when
    Category result = categoryProvider.getCategoryEntityById(categoryId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("음료");
    verify(categoryMapper).getCategoryEntityById(categoryId);
  }

  @Test
  @DisplayName("Category ID로 조회 시 null 반환하면 null 리턴")
  void getCategoryEntityById_null() {
    // given
    Long categoryId = 1L;
    when(categoryMapper.getCategoryEntityById(categoryId)).thenReturn(null);

    // when
    Category result = categoryProvider.getCategoryEntityById(categoryId);

    // then
    assertThat(result).isNull();
    verify(categoryMapper).getCategoryEntityById(categoryId);
  }
}
