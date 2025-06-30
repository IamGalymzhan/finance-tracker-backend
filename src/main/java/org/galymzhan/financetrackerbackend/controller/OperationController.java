package org.galymzhan.financetrackerbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.OperationResponseDto;
import org.galymzhan.financetrackerbackend.service.OperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/operations")
public class OperationController {

    private final OperationService operationService;

    @GetMapping
    public ResponseEntity<List<OperationResponseDto>> getAll() {
        return ResponseEntity.ok(operationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(operationService.getById(id));
    }

    @PostMapping
    public ResponseEntity<OperationResponseDto> create(@Valid @RequestBody OperationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operationService.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OperationResponseDto> update(@PathVariable Long id, @Valid @RequestBody OperationRequestDto dto) {
        return ResponseEntity.ok(operationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        operationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
