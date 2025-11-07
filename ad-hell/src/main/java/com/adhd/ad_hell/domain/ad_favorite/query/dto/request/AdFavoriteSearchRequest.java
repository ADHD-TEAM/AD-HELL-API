package com.adhd.ad_hell.domain.ad_favorite.query.dto.request;

import lombok.Getter;


@Getter
public class AdFavoriteSearchRequest {
    private  Long userId;
    private  Integer page = 1;
    private  Integer size = 20;
    private  String keyword;

    // -----------------------------
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
