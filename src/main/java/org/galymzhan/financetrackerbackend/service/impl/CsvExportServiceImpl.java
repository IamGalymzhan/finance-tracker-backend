package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.galymzhan.financetrackerbackend.dto.filter.OperationFilterDto;
import org.galymzhan.financetrackerbackend.dto.response.OperationCsvExportDto;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.mapper.OperationMapper;
import org.galymzhan.financetrackerbackend.repository.OperationRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.CsvExportService;
import org.galymzhan.financetrackerbackend.specification.OperationSpecification;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvExportServiceImpl implements CsvExportService {

    private final OperationRepository operationRepository;
    private final AuthenticationService authenticationService;
    private final OperationMapper operationMapper;

    private static final String[] CSV_HEADERS = {
            "Date", "Description", "Amount", "Type", "Category",
            "Account In", "Account Out", "Tags", "Note", "Created Date"
    };

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public Resource exportOperationsToCsv(OperationFilterDto filters) {
        try {
            User currentUser = authenticationService.getCurrentUser();

            Specification<Operation> spec = OperationSpecification.withFilters(filters, currentUser);
            List<Operation> operations = operationRepository.findAll(spec);

            List<OperationCsvExportDto> exportData = operations.stream()
                    .map(operationMapper::toCsvExportDto)
                    .toList();

            return generateCsvResource(exportData);

        } catch (IOException e) {
            log.error("Error generating CSV export", e);
            throw new RuntimeException("Failed to generate CSV export", e);
        }
    }

    private Resource generateCsvResource(List<OperationCsvExportDto> exportData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(CSV_HEADERS)
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (OperationCsvExportDto operation : exportData) {
                csvPrinter.printRecord(
                        operation.getDate().format(DATE_FORMATTER),
                        operation.getDescription(),
                        operation.getAmount(),
                        operation.getType(),
                        operation.getCategory(),
                        operation.getAccountIn(),
                        operation.getAccountOut(),
                        operation.getTags(),
                        operation.getNote(),
                        operation.getCreatedDate().format(DATETIME_FORMATTER)
                );
            }
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }
}