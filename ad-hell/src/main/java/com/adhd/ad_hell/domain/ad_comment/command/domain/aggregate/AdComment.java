package com.adhd.ad_hell.domain.ad_comment.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ad_comment")
@NoArgsConstructor
public class AdComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adCommentId;

    private Long userId;
    private Long adId;
    private String content;

    public void update(String content) {
        this.content = content;
    }

    @Builder
    private AdComment(Long userId, Long adId, String content) {
        // 여기서 필드 매핑이 뒤바뀌지 않도록 정확히 할당
        this.userId = userId;
        this.adId = adId;
        this.content = content;
    }

}
