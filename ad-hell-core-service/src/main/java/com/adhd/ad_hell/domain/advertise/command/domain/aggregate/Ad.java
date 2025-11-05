package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;


import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="ad")
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
    private AdStatus status = AdStatus.ACTIVATE;

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

    /* ====== 빌더 ====== */
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

    /* ====== DTO → Entity 변환 ====== */
    public static Ad fromCreateDto(AdCreateRequest dto) {
        return Ad.builder()
                .userId(dto.getUserId())
                .categoryId(dto.getCategoryId())
                .title(dto.getTitle())
                .status(dto.getStatus())
                .like_count(dto.getLike_count())
                .bookmark_count(dto.getBookmark_count())
                .comment_count(dto.getComment_count())
                .view_count(dto.getView_count())
                .build();
    }



}
