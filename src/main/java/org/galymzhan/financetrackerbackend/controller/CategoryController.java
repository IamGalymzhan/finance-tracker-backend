package org.galymzhan.financetrackerbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.CategoryRequestDto;
import org.galymzhan.financetrackerbackend.dto.CategoryResponseDto;
import org.galymzhan.financetrackerbackend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable Long id, @Valid @RequestBody CategoryRequestDto dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
