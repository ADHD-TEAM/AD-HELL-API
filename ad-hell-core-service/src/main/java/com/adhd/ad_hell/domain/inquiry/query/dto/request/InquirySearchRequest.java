package com.adhd.ad_hell.domain.inquiry.query.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquirySearchRequest {

    private Long userId;       // 회원 ID (내 문의 전용)
    private Integer page = 1;  // 기본 페이지 번호
    private Integer size = 20; // 기본 페이지 크기
    private String keyword;    // 검색어 (제목/내용 등)
    private String answered;   // 답변 여부 필터 ("Y" or "N")

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
