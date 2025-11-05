package com.adhd.ad_hell.domain.category.query.service.provider;

import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.query.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CategoryProviderImpl implements CategoryProvider{

  private final CategoryMapper categoryMapper;

  @Transactional(readOnly = true)
  @Override
  public Category getCategoryEntityById(Long categoryId) {
    return categoryMapper.getCategoryEntityById(categoryId);
  }
}
