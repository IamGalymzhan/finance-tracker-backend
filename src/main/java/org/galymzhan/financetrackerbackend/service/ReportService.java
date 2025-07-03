package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.ReportOverviewDto;

import java.time.LocalDate;

public interface ReportService {

    ReportOverviewDto getReportOverview(LocalDate startDate, LocalDate endDate);
}
