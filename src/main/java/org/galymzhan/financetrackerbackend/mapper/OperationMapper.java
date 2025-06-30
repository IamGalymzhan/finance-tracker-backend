package org.galymzhan.financetrackerbackend.mapper;

import org.galymzhan.financetrackerbackend.dto.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.OperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "accountIn", source = "accountIn")
    @Mapping(target = "accountOut", source = "accountOut")
    @Mapping(target = "tags", source = "tags")
    OperationResponseDto toResponseDto(Operation operation);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "accountIn", ignore = true)
    @Mapping(target = "accountOut", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Operation toEntity(OperationRequestDto operationRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "accountIn", ignore = true)
    @Mapping(target = "accountOut", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntity(@MappingTarget Operation operation, OperationRequestDto operationRequestDto);
}
