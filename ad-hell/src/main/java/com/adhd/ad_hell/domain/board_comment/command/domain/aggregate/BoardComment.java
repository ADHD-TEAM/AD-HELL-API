package com.adhd.ad_hell.domain.board_comment.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import com.adhd.ad_hell.domain.user.command.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BoardComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    // 댓글 내용
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    // 회원 FK
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 게시판 FK
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;



    // 필수는 아니지만 도메인 규칙이 조금 많은 현재 상황에서는 Entity에 예외 처리 코드가 있는게 좋음
    // 추후에 확장성 고려 시 Service에 분리해도 괜찮음.

    // 수정
    public void updateContent(String newContent, Long requestUserId) {
        if (!this.user.getUserId().equals(requestUserId)) {
            throw new SecurityException("본인 댓글만 수정할 수 있습니다.");
        }
        this.content = newContent;
    }

    // 삭제 권한 검증
    public void assertOwner(Long requestUserId) {
        if (!this.user.getUserId().equals(requestUserId)) {
            throw new SecurityException("본인 댓글만 삭제할 수 있습니다.");
        }
    }
}
