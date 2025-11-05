package com.adhd.ad_hell.domain.user.command.repository;

import com.adhd.ad_hell.domain.user.command.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointCommandRepository extends JpaRepository<PointHistory, Long> {

}
