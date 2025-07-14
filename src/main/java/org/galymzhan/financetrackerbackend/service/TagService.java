package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.request.TagRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.TagResponseDto;

import java.util.List;

public interface TagService {

    List<TagResponseDto> getAll();

    TagResponseDto getById(Long id);

    TagResponseDto create(TagRequestDto tagRequestDto);

    TagResponseDto update(Long id, TagRequestDto tagRequestDto);

    void delete(Long id);
}
