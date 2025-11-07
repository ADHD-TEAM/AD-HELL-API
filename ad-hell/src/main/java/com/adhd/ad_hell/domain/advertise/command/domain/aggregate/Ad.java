package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;


import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="ad")
@SQLDelete(sql = "UPDATE ad SET status = 'DEACTIVATED' where ad_id = ?")
public class Ad extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long categoryId;
    @Column(nullable = false, length = 50)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdStatus status;

    private int like_count;

    private int bookmark_count;

    private int comment_count;

    private int view_count;

    // 파일 다건: 고아삭제 + 영속전이
    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdFile> files = new ArrayList<>();

    /* ========= 연관관계 편의 메서드 ========= */

    public void addFile(AdFile file) {
        files.add(file);
        file.setAd(this);
    }

    public void removeFile(AdFile file) {
        files.remove(file);
        file.setAd(null);           // <- 양방향 일관성 + 고아 설정
    }

    public void clearFiles() {
        for (AdFile f : new ArrayList<>(files)) {
            removeFile(f);          // removeFile 사용(양쪽 끊기)
        }
    }

    public void updateAd(
            String title,
            int like_count,
            int bookmark_count,
            int comment_count,
            int view_count
    ) {
        this.title = title;
        this.like_count = like_count;
        this.bookmark_count = bookmark_count;
        this.comment_count = comment_count;
        this.view_count = view_count;
    }

    @Builder
    private Ad(Long userId, Long categoryId, String title,
               AdStatus status,int like_count,int bookmark_count,
               int comment_count,int view_count) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.status = status;
        this.like_count = 0;
        this.bookmark_count = 0;
        this.comment_count = 0;
        this.view_count = 0;
    }

    public static Ad fromCreateDto(AdCreateRequest dto, Long userId) {
        return Ad.builder()
                 .userId(userId)
                 .categoryId(dto.getCategoryId())
                 .title(dto.getTitle())
                 .status(AdStatus.ACTIVATE)
                 .like_count(0)
                 .bookmark_count(0)
                 .comment_count(0)
                 .view_count(0)
                 .build();
    }

    public void updateAdInfo(String title, Long categoryId) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (categoryId != null) {
            this.categoryId = categoryId;
        }
    }
}
