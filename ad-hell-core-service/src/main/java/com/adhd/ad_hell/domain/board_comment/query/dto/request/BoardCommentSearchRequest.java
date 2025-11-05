package com.adhd.ad_hell.domain.board_comment.query.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCommentSearchRequest {

    // ----------------------------
    // 페이징
    // ----------------------------
    private Integer page = 1; // 기본 1페이지
    private Integer size = 20; // 기본 페이지 크기

    // ----------------------------
    // 검색 조건
    // ----------------------------
    private Long boardId;     // 특정 게시글 ID (게시글별 댓글 조회용)
    private Long writerId;    // 작성자 ID (내 댓글 조회용)
    private String keyword;   // 댓글 내용 검색

    // ----------------------------
    // 정렬 조건
    // ----------------------------
    private String sortBy = "createdAt"; // 기본 정렬 기준
    private String direction = "DESC";   // 기본 내림차순

    // ----------------------------
    // 보정 메서드 (잘못된 입력 방어)
    // ----------------------------

    /** 페이지 번호 보정 */
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
        if (size > 100) {
            return 100;
        }
        return size;
    }

    /** LIMIT OFFSET 계산용 */
    public int getOffset() {
        return (getPage() - 1) * getSize();
    }

    /** LIMIT 개수 */
    public int getLimit() {
        return getSize();
    }
}