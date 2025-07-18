package org.galymzhan.financetrackerbackend.mapper;

import org.galymzhan.financetrackerbackend.dto.request.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.AccountResponseDto;
import org.galymzhan.financetrackerbackend.entity.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponseDto toResponseDto(Account account);

    Account toEntity(AccountRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Account account, AccountRequestDto dto);
}
