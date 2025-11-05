package com.adhd.ad_hell.domain.category.query.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {
  private Long id;
  private String name;
  private String description;
  private String status;
  private Long parentId;
  private List<CategoryTreeResponse> children = new ArrayList<>();
}