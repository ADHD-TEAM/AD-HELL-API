package com.adhd.ad_hell.domain.inquiry.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.user.command.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "inquiry")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 관리자 답변 (nullable)
    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    /** Builder 생성자 */
    @Builder
    private Inquiry(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /** 연관관계 주입 */
    public void linkUser(User user) { this.user = user; }
    public void linkCategory(Category category) { this.category = category; }

    /** 관리자 답변/수정 */
    public void answer(String response) {
        this.response = response;
        this.answeredAt = LocalDateTime.now();
    }
}
