package com.adhd.ad_hell.domain.advertise.command.infrastructure.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdLike;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdLikeId;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdFileRepository;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdLikeRepository;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdLikeRepository extends AdLikeRepository, JpaRepository<AdLike, AdLikeId> {

}
