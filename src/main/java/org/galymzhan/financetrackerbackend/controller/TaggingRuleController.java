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
import org.galymzhan.financetrackerbackend.dto.background.TaggingResult;
import org.galymzhan.financetrackerbackend.dto.request.TaggingRuleRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.ExceptionDto;
import org.galymzhan.financetrackerbackend.dto.response.TaggingRuleResponseDto;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.BackgroundTaggingService;
import org.galymzhan.financetrackerbackend.service.TaggingRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tagging-rules")
@Tag(name = "Tagging Rules", description = "Manage auto-tagging rules for operations")
@SecurityRequirement(name = "Bearer Authentication")
public class TaggingRuleController {

    private final TaggingRuleService taggingRuleService;
    private final AuthenticationService authenticationService;
    private final BackgroundTaggingService backgroundTaggingService;

    @Operation(summary = "Get all tagging rules", description = "Retrieve all user's auto-tagging rules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rules retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<TaggingRuleResponseDto>> getAll() {
        return ResponseEntity.ok(taggingRuleService.getAll());
    }

    @Operation(summary = "Get tagging rule by ID", description = "Retrieve specific tagging rule details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Rule not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaggingRuleResponseDto> getById(
            @Parameter(description = "Rule ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(taggingRuleService.getById(id));
    }

    @Operation(summary = "Create tagging rule", description = "Create new auto-tagging rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping
    public ResponseEntity<TaggingRuleResponseDto> create(@Valid @RequestBody TaggingRuleRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taggingRuleService.create(requestDto));
    }

    @Operation(summary = "Update tagging rule", description = "Update existing tagging rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Rule not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TaggingRuleResponseDto> update(
            @Parameter(description = "Rule ID", required = true) @PathVariable Long id,
            @Valid @RequestBody TaggingRuleRequestDto requestDto) {
        return ResponseEntity.ok(taggingRuleService.update(id, requestDto));
    }

    @Operation(summary = "Delete tagging rule", description = "Delete tagging rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rule deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Rule not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Rule ID", required = true) @PathVariable Long id) {
        taggingRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Apply tagging rules to specific operations",
            description = "Apply all active tagging rules to a list of operations in the background"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Tagging process started successfully",
                    content = @Content(schema = @Schema(implementation = TaggingResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @PostMapping("/apply-to-operations")
    public ResponseEntity<CompletableFuture<TaggingResult>> applyRulesToOperations(
            @Parameter(
                    description = "List of operation IDs to apply tagging rules to",
                    required = true,
                    example = "[1, 2, 3, 4, 5]"
            )
            @RequestBody List<Long> operationIds) {
        User user = authenticationService.getCurrentUser();
        CompletableFuture<TaggingResult> result = backgroundTaggingService.processOperationsInBackground(user, operationIds);
        return ResponseEntity.accepted().body(result);
    }

    @Operation(
            summary = "Reapply all tagging rules",
            description = "Reapply all active tagging rules to all user operations in the background"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Rule reapplication process started successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))
            )
    })
    @PostMapping("/reapply-all")
    public ResponseEntity<Void> reapplyAllRules() {
        User user = authenticationService.getCurrentUser();
        backgroundTaggingService.reapplyAllRules(user);
        return ResponseEntity.accepted().build();
    }
}