package com.adhd.ad_hell.common.dto;

import lombok.Builder;
import lombok.Getter;


/**
 * 모든 도메인에서 공통적으로 참조하는 카테고리 정보 DTO
 * - View 전용 구조 (JPA 연관 없음)
 * - MSA 전환 시 Response Contract로 재활용 가능
 */
@Getter
@Builder
public class CategoryInfoResponse {
  private Long categoryId;
  private String categoryName;
  private Long parentId;
  private String parentName;
}
