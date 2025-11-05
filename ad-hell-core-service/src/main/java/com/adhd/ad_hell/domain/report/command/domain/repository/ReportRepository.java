package com.adhd.ad_hell.domain.report.command.domain.repository;

import com.adhd.ad_hell.domain.report.command.domain.aggregate.Report;

public interface ReportRepository {

  Report save(Report report);
}
