package com.adhd.ad_hell.domain.report.command.infrastructure.repository;

import com.adhd.ad_hell.domain.report.command.domain.aggregate.Report;
import com.adhd.ad_hell.domain.report.command.domain.repository.ReportRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReportRepository extends ReportRepository, JpaRepository<Report, Long> {

}
