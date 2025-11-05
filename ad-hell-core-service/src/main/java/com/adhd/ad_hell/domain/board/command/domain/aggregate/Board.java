package com.adhd.ad_hell.domain.board.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.user.command.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "board_title", nullable = false)
    private String title;

    @Lob
    @Column(name = "board_content", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "board_status", nullable = false, length = 1)
    private String status = "Y";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;


    // --- 비즈니스 ---
    public void increaseViewCount() { this.viewCount = (viewCount == null ? 0L : viewCount) + 1; }

    public void updateBoard(String title, String content, Category category, String status) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (category != null) this.category = category;
        if (status != null) this.status = status;
    }

    // 서비스에서 Builder 안 쓰고 간단히 엔티티를 만들기 위한 정적 팩토리
    public static Board create(User writer, Category category, String title, String content, String status) {
        return Board.builder()
                .writer(writer)
                .category(category)
                .title(title)
                .content(content)
                .status(status != null ? status : "Y")
                .viewCount(0L)
                .build();
    }


}
