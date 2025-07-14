package org.galymzhan.financetrackerbackend.mapper;

import org.galymzhan.financetrackerbackend.dto.request.UserProfileUpdateDto;
import org.galymzhan.financetrackerbackend.dto.response.UserProfileResponseDto;
import org.galymzhan.financetrackerbackend.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileResponseDto toResponseDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget User user, UserProfileUpdateDto dto);
}
