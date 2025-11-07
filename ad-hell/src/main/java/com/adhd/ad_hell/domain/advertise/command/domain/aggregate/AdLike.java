package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Embeddable
@Getter
public class AdLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adLikeId;
    private Long adId;
    private Long userId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
