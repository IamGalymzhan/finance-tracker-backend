package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.request.CategoryRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.CategoryResponseDto;
import org.galymzhan.financetrackerbackend.entity.Category;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.CategoryMapper;
import org.galymzhan.financetrackerbackend.repository.CategoryRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuthenticationService authenticationService;

    @Override
    @Cacheable(value = "user-categories", keyGenerator = "userAwareKeyGenerator")
    public List<CategoryResponseDto> getAll() {
        User user = authenticationService.getCurrentUser();
        return categoryRepository.findAllByUser(user).stream()
                .map(categoryMapper::toResponseDto)
                .toList();
    }

    @Override
    @Cacheable(value = "user-category-by-id", keyGenerator = "userAwareKeyGenerator")
    public CategoryResponseDto getById(Long id) {
        User user = authenticationService.getCurrentUser();
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return categoryMapper.toResponseDto(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-categories", "user-category-by-id"}, allEntries = true)
    public CategoryResponseDto create(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        category.setUser(authenticationService.getCurrentUser());
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDto(savedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-categories", "user-category-by-id"}, allEntries = true)
    public CategoryResponseDto update(Long id, CategoryRequestDto categoryRequestDto) {
        User user = authenticationService.getCurrentUser();
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        categoryMapper.updateEntity(category, categoryRequestDto);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDto(updatedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user-categories", "user-category-by-id"}, allEntries = true)
    public void delete(Long id) {
        User user = authenticationService.getCurrentUser();
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.delete(category);
    }
}
