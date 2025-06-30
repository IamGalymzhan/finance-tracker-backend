package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.CategoryRequestDto;
import org.galymzhan.financetrackerbackend.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getAll();

    CategoryResponseDto getById(Long id);

    CategoryResponseDto create(CategoryRequestDto accountRequestDto);

    CategoryResponseDto update(Long id, CategoryRequestDto accountRequestDto);

    void delete(Long id);
}
