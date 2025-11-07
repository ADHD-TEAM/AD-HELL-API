package com.adhd.ad_hell.domain.category.query.service;

import com.adhd.ad_hell.domain.category.query.dto.response.CategoryDetailResponse;
import com.adhd.ad_hell.domain.category.query.dto.response.CategoryTreeResponse;
import com.adhd.ad_hell.domain.category.query.mapper.CategoryMapper;
import com.adhd.ad_hell.exception.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService {

  private final CategoryMapper categoryMapper;

  public CategoryDetailResponse getCategoryDetail(Long categoryId) {
    CategoryDetailResponse result = categoryMapper.findCategoryById(categoryId);
    if (result == null) {
      throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
    }
    return result;
  }

  public List<CategoryTreeResponse> getCategoryTree(String keyword) {
    List<CategoryTreeResponse> all = categoryMapper.findAllCategories(keyword);

//    Map<Long, CategoryTreeResponse> map =
//    all.stream().collect(Collectors.toMap(CategoryTreeResponse::getId, c -> c));

    Map<Long, CategoryTreeResponse> map = new HashMap<>();
    for (CategoryTreeResponse category : all) {
      map.put(category.getId(), category);
    }

    List<CategoryTreeResponse> roots = new ArrayList<>();
    for (CategoryTreeResponse category : all) {
      if (category.getParentId() == null) {
        roots.add(category);
      } else {
        CategoryTreeResponse parent = map.get(category.getParentId());
        if (parent != null) {
          parent.getChildren().add(category);
        }
      }
    }

    return roots;
  }
}
