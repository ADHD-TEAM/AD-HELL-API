package com.adhd.ad_hell.domain.category.query.service;

import com.adhd.ad_hell.domain.category.query.dto.response.CategoryDetailResponse;
import com.adhd.ad_hell.domain.category.query.dto.response.CategoryTreeResponse;
import com.adhd.ad_hell.domain.category.query.mapper.CategoryMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    for (CategoryTreeResponse c : all) {
      map.put(c.getId(), c);
    }

    List<CategoryTreeResponse> roots = new ArrayList<>();
    for (CategoryTreeResponse c : all) {
      if (c.getParentId() == null) {
        roots.add(c);
      } else {
        CategoryTreeResponse parent = map.get(c.getParentId());
        if (parent != null) {
          parent.getChildren().add(c);
        } else {
          roots.add(c);
        }
      }
    }

    return roots;
  }
}
