package com.adhd.ad_hell.domain.category.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateCategoryRequest {
  private final String name;
  private final String description;
}
