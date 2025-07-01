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
import org.galymzhan.financetrackerbackend.dto.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.AccountResponseDto;
import org.galymzhan.financetrackerbackend.dto.ExceptionDto;
import org.galymzhan.financetrackerbackend.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Manage financial accounts (bank, cash, credit cards)")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Get all accounts", description = "Retrieve all user accounts with balances and details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAll() {
        return ResponseEntity.ok(accountService.getAll());
    }

    @Operation(summary = "Get account by ID", description = "Retrieve specific account details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "404", description = "Account not found",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getById(
        @Parameter(description = "Account ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @Operation(summary = "Create new account", description = "Create new financial account with name, type, and initial balance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized access",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@Valid @RequestBody AccountRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(dto));
    }

    @Operation(summary = "Update account", description = "Update account details like name, color, or icon")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized access",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "404", description = "Account not found",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponseDto> update(
        @Parameter(description = "Account ID", required = true) @PathVariable Long id, 
        @Valid @RequestBody AccountRequestDto dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }

    @Operation(summary = "Delete account", description = "Delete account and all associated transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
        @ApiResponse(responseCode = "404", description = "Account not found",
            content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "Account ID", required = true) @PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
