package org.galymzhan.financetrackerbackend.mapper;

import org.galymzhan.financetrackerbackend.dto.request.TagRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.TagResponseDto;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponseDto toResponseDto(Tag tag);

    Tag toEntity(TagRequestDto tagRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Tag tag, TagRequestDto tagRequestDto);
}
