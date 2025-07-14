package org.galymzhan.financetrackerbackend.mapper;

import org.galymzhan.financetrackerbackend.dto.request.OperationRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.OperationCsvExportDto;
import org.galymzhan.financetrackerbackend.dto.response.OperationResponseDto;
import org.galymzhan.financetrackerbackend.entity.Category;
import org.galymzhan.financetrackerbackend.entity.Operation;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    OperationResponseDto.AccountSummaryDto toAccountSummaryDto(org.galymzhan.financetrackerbackend.entity.Account account);

    @Mapping(target = "date", source = "date")
    @Mapping(target = "description", source = "name")
    @Mapping(target = "amount", source = ".", qualifiedByName = "formatAmount")
    @Mapping(target = "type", source = "operationType")
    @Mapping(target = "category", source = ".", qualifiedByName = "formatCategory")
    @Mapping(target = "accountIn", source = "accountIn.name")
    @Mapping(target = "accountOut", source = "accountOut.name")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "formatTags")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "createdDate", source = "createdAt")
    OperationCsvExportDto toCsvExportDto(Operation operation);

    @Named("formatAmount")
    default BigDecimal formatAmount(Operation operation) {
        BigDecimal amount = operation.getAmount();
        return switch (operation.getOperationType()) {
            case EXPENSE -> amount.negate();
            case INCOME, TRANSFER -> amount;
        };
    }

    @Named("formatCategory")
    default String formatCategory(Operation operation) {
        return buildCategoryPath(operation.getCategory());
    }

    default String buildCategoryPath(Category category) {
        List<String> categoryNames = new ArrayList<>();
        Category current = category;
        Set<Long> visitedIds = new HashSet<>();

        while (current != null) {
            if (visitedIds.contains(current.getId())) {
                break;
            }
            visitedIds.add(current.getId());

            categoryNames.add(current.getName());
            current = current.getParentCategory();
        }

        Collections.reverse(categoryNames);
        return String.join(" > ", categoryNames);
    }

    @Named("formatTags")
    default String formatTags(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(" "));
    }
}
