package com.adhd.ad_hell.domain.category.command.domain.repository;

import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import java.util.Optional;

public interface CategoryRepository {
  Optional<Category> findById(Long categoryId);
  Category save(Category category);
}
