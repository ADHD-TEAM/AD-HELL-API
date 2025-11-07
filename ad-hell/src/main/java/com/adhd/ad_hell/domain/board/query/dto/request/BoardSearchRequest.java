package com.adhd.ad_hell.domain.board.query.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchRequest {

    // 페이징
    private Integer page = 1;
    private Integer size = 10;

    // 검색 조건
    private Long categoryId;
    private String keyword;
    private String status;

    //정렬 조건
    private String sortBy = "createdAt";// 정렬 기준 필드
    private String direction = "DESC"; // 내림차순


    //보정 메서드
    // page가 null이거나 1보다 작으면 1로 보정
    public int getPage() {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    public int getSize() {
        if (size == null || size < 1) {
            return 10;
        }
        // 너무 큰 요청 방지
        if (size > 100) {
            return 100;
        }
        return size;
    }

    public int getOffset() {
        return (getPage() - 1) * getSize();
    }

    public int getLimit() {
        return getSize();
    }
}
