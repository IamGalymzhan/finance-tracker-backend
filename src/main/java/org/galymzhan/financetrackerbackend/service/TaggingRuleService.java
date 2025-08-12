package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.request.TaggingRuleRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.TaggingRuleResponseDto;
import org.galymzhan.financetrackerbackend.entity.rules.TaggingRule;

import java.util.List;

public interface TaggingRuleService {

    List<TaggingRuleResponseDto> getAll();

    TaggingRuleResponseDto getById(Long id);

    TaggingRuleResponseDto create(TaggingRuleRequestDto requestDto);

    TaggingRuleResponseDto update(Long id, TaggingRuleRequestDto requestDto);

    void delete(Long id);

    List<TaggingRule> getUserRules();
}
