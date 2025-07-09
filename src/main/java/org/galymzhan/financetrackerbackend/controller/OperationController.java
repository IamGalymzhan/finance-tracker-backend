package org.galymzhan.financetrackerbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.ExceptionDto;
import org.galymzhan.financetrackerbackend.dto.OperationFilterDto;
import org.galymzhan.financetrackerbackend.dto.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.OperationResponseDto;
import org.galymzhan.financetrackerbackend.service.CsvExportService;
import org.galymzhan.financetrackerbackend.service.OperationService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/operations")
@Tag(name = "Operations", description = "Manage financial transactions (income, expense, transfer)")
@SecurityRequirement(name = "Bearer Authentication")
public class OperationController {

    private final OperationService operationService;
    private final CsvExportService csvExportService;

    @Operation(summary = "Get all operations", description = "Retrieve all user operations with details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<OperationResponseDto>> getAllFiltered(
            @ParameterObject OperationFilterDto filters,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(operationService.getAllFiltered(filters, pageable));
    }

    @Operation(summary = "Get operation by ID", description = "Retrieve specific operation details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Operation not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<OperationResponseDto> getById(
            @Parameter(description = "Operation ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(operationService.getById(id));
    }

    @Operation(summary = "Create new operation", description = "Create new operation (income, expense, or transfer)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping
    public ResponseEntity<OperationResponseDto> create(@Valid @RequestBody OperationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operationService.create(dto));
    }

    @Operation(summary = "Update operation", description = "Update operation details like amount, category, or note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Operation not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<OperationResponseDto> update(
            @Parameter(description = "Operation ID", required = true) @PathVariable Long id,
            @Valid @RequestBody OperationRequestDto dto) {
        return ResponseEntity.ok(operationService.update(id, dto));
    }

    @Operation(summary = "Delete operation", description = "Delete operation and update account balances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operation deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Operation not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Operation ID", required = true) @PathVariable Long id) {
        operationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Export operations to csv", description = "Export filtered operations in the csv format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV file generated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportToCsv(@ParameterObject OperationFilterDto filters) {
        Resource csvResource = csvExportService.exportOperationsToCsv(filters);

        String filename = "operations_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(csvResource);
    }
}
