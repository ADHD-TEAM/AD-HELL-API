package com.adhd.ad_hell.domain.ranking.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="ad_rank")
public class AdRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankId;
    private Long categoryId;
    private Long adId;
    private float score;
    @CreatedDate
    @Column(name = "ranked_time", nullable = false)
    private LocalDateTime rankedTime;
}
