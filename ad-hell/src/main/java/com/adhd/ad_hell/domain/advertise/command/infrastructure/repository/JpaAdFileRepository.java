package com.adhd.ad_hell.domain.advertise.command.infrastructure.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdFileRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdFileRepository extends AdFileRepository, JpaRepository<AdFile, Long> {

}
