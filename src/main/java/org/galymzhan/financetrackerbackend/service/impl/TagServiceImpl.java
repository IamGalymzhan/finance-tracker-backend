package org.galymzhan.financetrackerbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.TagRequestDto;
import org.galymzhan.financetrackerbackend.dto.TagResponseDto;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.TagMapper;
import org.galymzhan.financetrackerbackend.repository.TagRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final AuthenticationService authenticationService;

    @Override
    public List<TagResponseDto> getAll() {
        User user = authenticationService.getCurrentUser();
        return tagRepository.findAllByUser(user)
                .stream()
                .map(tagMapper::toResponseDto)
                .toList();
    }

    @Override
    public TagResponseDto getById(Long id) {
        User user = authenticationService.getCurrentUser();
        Tag tag = tagRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Tag not found"));
        return tagMapper.toResponseDto(tag);
    }

    @Override
    public TagResponseDto create(TagRequestDto tagRequestDto) {
        User user = authenticationService.getCurrentUser();
        Tag tag = tagMapper.toEntity(tagRequestDto);
        tag.setUser(user);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toResponseDto(savedTag);
    }

    @Override
    public TagResponseDto update(Long id, TagRequestDto tagRequestDto) {
        User user = authenticationService.getCurrentUser();
        Tag tag = tagRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Tag not found"));
        tagMapper.updateEntity(tag, tagRequestDto);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toResponseDto(savedTag);
    }

    @Override
    public void delete(Long id) {
        User user = authenticationService.getCurrentUser();
        Tag tag = tagRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Tag not found"));
        tagRepository.delete(tag);
    }
}
