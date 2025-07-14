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
import org.galymzhan.financetrackerbackend.dto.request.TagRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.ExceptionDto;
import org.galymzhan.financetrackerbackend.dto.response.TagResponseDto;
import org.galymzhan.financetrackerbackend.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
@Tag(name = "Tags", description = "Manage operation tags for additional labeling and filtering")
@SecurityRequirement(name = "Bearer Authentication")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Get all tags", description = "Retrieve all user tags for labeling operations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }

    @Operation(summary = "Get tag by ID", description = "Retrieve specific tag details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getById(
            @Parameter(description = "Tag ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(tagService.getById(id));
    }

    @Operation(summary = "Create new tag", description = "Create new tag with name and color for labeling operations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping
    public ResponseEntity<TagResponseDto> create(@Valid @RequestBody TagRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(dto));
    }

    @Operation(summary = "Update tag", description = "Update tag details like name or color")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TagResponseDto> update(
            @Parameter(description = "Tag ID", required = true) @PathVariable Long id,
            @Valid @RequestBody TagRequestDto dto) {
        return ResponseEntity.ok(tagService.update(id, dto));
    }

    @Operation(summary = "Delete tag", description = "Delete tag and remove from all operations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Tag ID", required = true) @PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
