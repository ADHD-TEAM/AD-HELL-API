package com.adhd.ad_hell.domain.announcement.query.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementSearchRequest {

    //  검색 조건
    private Integer page = 1;     // 요청 페이지 (null이면 기본값)
    private Integer size = 20;     // 페이지당 항목 수 (null이면 기본값)
    private String keyword;   // 제목 + 내용 LIKE 검색용 키워드
    private String status;    // 게시 상태 (Y/N/null)

    // 보정 메서드 (잘못된 입력 방어)
    // -------------------------------

    /** 페이지 보정 */
    public int getPage() {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    /** 페이지 크기 보정 */
    public int getSize() {
        if (size == null || size < 1) {
            return 20;
        }
        // 너무 큰 요청 방지
        if (size > 100) {
            return 100;
        }
        return size;
    }

    /** LIMIT OFFSET 계산용 */
    public int getOffset() {
        return (getPage() - 1) * getSize();
    }

    public int getLimit() {
        return getSize();
    }
}
