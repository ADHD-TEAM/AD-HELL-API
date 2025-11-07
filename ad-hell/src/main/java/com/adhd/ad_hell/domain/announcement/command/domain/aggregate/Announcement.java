package com.adhd.ad_hell.domain.announcement.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.user.command.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "announcement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Announcement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 999, nullable = false)
    private String content;

    @Column(length = 1, nullable = false)
    private String status; // 'Y' 게시 / 'N' 게시중단

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    public void update(String title, String content, String status) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (status != null) this.status = status;
    }
}
