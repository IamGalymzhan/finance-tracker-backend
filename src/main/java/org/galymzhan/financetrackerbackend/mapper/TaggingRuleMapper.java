package org.galymzhan.financetrackerbackend.mapper;

import org.galymzhan.financetrackerbackend.dto.request.TaggingConditionRequestDto;
import org.galymzhan.financetrackerbackend.dto.request.TaggingRuleRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.TaggingConditionResponseDto;
import org.galymzhan.financetrackerbackend.dto.response.TaggingRuleResponseDto;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingCondition;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface TaggingRuleMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    @Mapping(target = "tagsToApply", ignore = true)
    TaggingRule toEntity(TaggingRuleRequestDto requestDto);

    @Mapping(target = "conditions", source = "conditions")
    @Mapping(target = "tagsToApply", source = "tagsToApply")
    TaggingRuleResponseDto toResponseDto(TaggingRule rule);

    @Mapping(target = "rule", ignore = true)
    TaggingCondition toConditionEntity(TaggingConditionRequestDto requestDto);

    TaggingConditionResponseDto toConditionResponseDto(TaggingCondition condition);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    @Mapping(target = "tagsToApply", ignore = true)
    void updateEntity(@MappingTarget TaggingRule rule, TaggingRuleRequestDto requestDto);
}