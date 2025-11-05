package com.adhd.ad_hell.domain.advertise.command.infrastructure.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdRepository extends AdRepository, JpaRepository<Ad, Long> {
}