package com.adhd.ad_hell.domain.category.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponse {
  private Long id;
  private String name;
  private String description;
  private String status;

  private Long parentId;
  private String parentName;
}