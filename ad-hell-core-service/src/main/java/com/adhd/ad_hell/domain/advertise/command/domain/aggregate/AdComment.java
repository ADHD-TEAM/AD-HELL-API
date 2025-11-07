package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

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
@AllArgsConstructor
@Builder
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

}
