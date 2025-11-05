package com.adhd.ad_hell.domain.category.command.application.service;

import com.adhd.ad_hell.domain.category.command.application.dto.request.CreateCategoryRequest;
import com.adhd.ad_hell.domain.category.command.application.dto.request.UpdateCategoryRequest;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.CategoryStatus;
import com.adhd.ad_hell.domain.category.command.domain.repository.CategoryRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryCommandService {

  private final CategoryRepository categoryRepository;

  @Transactional
  public void createCategory(CreateCategoryRequest req) {

    Category parentCategory = null;
    if (req.getParentId() != null) {
      parentCategory = categoryRepository.findById(req.getParentId())
                                         .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    Category newCategory = Category.builder()
        .name(req.getName())
        .description(req.getDescription())
        .parent(parentCategory)
        .status(CategoryStatus.ACTIVATE)
        .build();

    categoryRepository.save(newCategory);
  }

  @Transactional
  public void updateCategory(Long categoryId, UpdateCategoryRequest req) {

    Category category = categoryRepository.findById(categoryId)
                                          .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

    category.updateInfo(req.getName(), req.getDescription());
  }

  @Transactional
  public void deleteCategory(Long categoryId) {
    Category category = categoryRepository.findById(categoryId)
                                          .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

    category.deleteRecursively();
  }
}
