package org.galymzhan.financetrackerbackend.service.impl;

import org.galymzhan.financetrackerbackend.dto.request.CategoryRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.CategoryResponseDto;
import org.galymzhan.financetrackerbackend.entity.Category;
import org.galymzhan.financetrackerbackend.entity.Direction;
import org.galymzhan.financetrackerbackend.entity.Role;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.CategoryMapper;
import org.galymzhan.financetrackerbackend.repository.CategoryRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User createTestUser() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        user.setId(1L);
        return user;
    }

    private Category createTestCategory() {
        Category category = Category.builder()
                .name("Test Category")
                .description("Test Description")
                .direction(Direction.EXPENSE)
                .targetAmount(new BigDecimal("500.00"))
                .color("#FF0000")
                .icon("shopping")
                .user(createTestUser())
                .build();
        category.setId(1L);
        return category;
    }

    private CategoryRequestDto createTestCategoryRequestDto() {
        return CategoryRequestDto.builder()
                .name("Test Category")
                .description("Test Description")
                .direction("EXPENSE")
                .targetAmount(new BigDecimal("500.00"))
                .color("#FF0000")
                .icon("shopping")
                .build();
    }

    private CategoryResponseDto createTestCategoryResponseDto() {
        return CategoryResponseDto.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .direction("EXPENSE")
                .targetAmount(new BigDecimal("500.00"))
                .color("#FF0000")
                .icon("shopping")
                .build();
    }

    @Test
    public void getAll_ShouldReturnCategoryList_WhenUserHas() {
        User user = createTestUser();
        List<Category> categories = List.of(createTestCategory());
        CategoryResponseDto responseDto = createTestCategoryResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAllByUser(user)).thenReturn(categories);
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(responseDto);

        List<CategoryResponseDto> result = categoryService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(categoryRepository).findAllByUser(user);
        verify(categoryMapper).toResponseDto(any(Category.class));
    }

    @Test
    public void getById_ShouldReturnCategory_WhenExists() {
        Long categoryId = 1L;
        User user = createTestUser();
        Category category = createTestCategory();
        CategoryResponseDto responseDto = createTestCategoryResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDto(category)).thenReturn(responseDto);

        CategoryResponseDto result = categoryService.getById(categoryId);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(categoryRepository).findByIdAndUser(categoryId, user);
        verify(categoryMapper).toResponseDto(category);
    }

    @Test
    public void getById_ShouldThrowNotFoundException_WhenNotFound() {
        Long categoryId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(authenticationService).getCurrentUser();
        verify(categoryRepository).findByIdAndUser(categoryId, user);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    public void create_ShouldCreateAndReturnCategory_WhenValidInput() {
        User user = createTestUser();
        CategoryRequestDto requestDto = createTestCategoryRequestDto();
        Category category = createTestCategory();
        Category savedCategory = createTestCategory();
        CategoryResponseDto responseDto = createTestCategoryResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toResponseDto(savedCategory)).thenReturn(responseDto);

        CategoryResponseDto result = categoryService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(categoryMapper).toEntity(requestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponseDto(savedCategory);
    }

    @Test
    public void update_ShouldUpdateAndReturnCategory_WhenCategoryExists() {
        Long categoryId = 1L;
        User user = createTestUser();
        CategoryRequestDto requestDto = createTestCategoryRequestDto();
        Category category = createTestCategory();
        Category updatedCategory = createTestCategory();
        CategoryResponseDto responseDto = createTestCategoryResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(updatedCategory);
        when(categoryMapper.toResponseDto(updatedCategory)).thenReturn(responseDto);

        CategoryResponseDto result = categoryService.update(categoryId, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(categoryRepository).findByIdAndUser(categoryId, user);
        verify(categoryMapper).updateEntity(category, requestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponseDto(updatedCategory);
    }

    @Test
    public void update_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        Long categoryId = 1L;
        User user = createTestUser();
        CategoryRequestDto requestDto = createTestCategoryRequestDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(categoryId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(authenticationService).getCurrentUser();
        verify(categoryRepository).findByIdAndUser(categoryId, user);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    public void delete_ShouldDeleteCategory_WhenCategoryExists() {
        Long categoryId = 1L;
        User user = createTestUser();
        Category category = createTestCategory();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.of(category));

        categoryService.delete(categoryId);

        verify(authenticationService).getCurrentUser();
        verify(categoryRepository).findByIdAndUser(categoryId, user);
        verify(categoryRepository).delete(category);
    }

    @Test
    public void delete_ShouldThrowNotFoundException_WhenCategoryNotFound() {
        Long categoryId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(categoryId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(authenticationService).getCurrentUser();
        verify(categoryRepository).findByIdAndUser(categoryId, user);
        verify(categoryRepository, never()).delete(any());
    }
}