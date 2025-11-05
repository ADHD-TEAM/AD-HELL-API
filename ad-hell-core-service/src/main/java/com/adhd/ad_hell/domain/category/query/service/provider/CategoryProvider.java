package com.adhd.ad_hell.domain.category.query.service.provider;

import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;

public interface CategoryProvider {
  Category getCategoryEntityById(Long categoryId);
}
