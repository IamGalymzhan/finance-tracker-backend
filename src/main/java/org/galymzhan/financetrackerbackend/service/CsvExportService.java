package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.filter.OperationFilterDto;
import org.springframework.core.io.Resource;

public interface CsvExportService {

    Resource exportOperationsToCsv(OperationFilterDto filters);
} 