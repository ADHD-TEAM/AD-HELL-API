package com.adhd.ad_hell.domain.category.query.mapper;

import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.query.dto.response.CategoryDetailResponse;
import com.adhd.ad_hell.domain.category.query.dto.response.CategoryTreeResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CategoryMapper {
  CategoryDetailResponse findCategoryById(@Param("categoryId") Long categoryId);
  List<CategoryTreeResponse> findAllCategories(@Param("keyword") String keyword);

  Category getCategoryEntityById(@Param("categoryId") Long categoryId);
}
