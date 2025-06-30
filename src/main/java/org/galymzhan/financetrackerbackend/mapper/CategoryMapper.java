package org.galymzhan.financetrackerbackend.mapper;

import org.galymzhan.financetrackerbackend.dto.CategoryRequestDto;
import org.galymzhan.financetrackerbackend.dto.CategoryResponseDto;
import org.galymzhan.financetrackerbackend.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDto toResponseDto(Category category);

    Category toEntity(CategoryRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Category category, CategoryRequestDto dto);
}