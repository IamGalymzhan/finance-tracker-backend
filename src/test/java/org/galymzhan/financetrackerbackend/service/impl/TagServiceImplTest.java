package org.galymzhan.financetrackerbackend.service.impl;

import org.galymzhan.financetrackerbackend.dto.request.TagRequestDto;
import org.galymzhan.financetrackerbackend.dto.response.TagResponseDto;
import org.galymzhan.financetrackerbackend.entity.Tag;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.entity.enums.Role;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;
import org.galymzhan.financetrackerbackend.mapper.TagMapper;
import org.galymzhan.financetrackerbackend.repository.TagRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TagServiceImpl tagService;

    private User createTestUser() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        user.setId(1L);
        return user;
    }

    private Tag createTestTag() {
        Tag tag = Tag.builder()
                .name("Test Tag")
                .color("#FF0000")
                .user(createTestUser())
                .build();
        tag.setId(1L);
        return tag;
    }

    private TagRequestDto createTestTagRequestDto() {
        return TagRequestDto.builder()
                .name("Test Tag")
                .color("#FF0000")
                .build();
    }

    private TagResponseDto createTestTagResponseDto() {
        return TagResponseDto.builder()
                .id(1L)
                .name("Test Tag")
                .color("#FF0000")
                .build();
    }

    @Test
    public void getAll_ShouldReturnTagList_WhenUserHasTags() {
        User user = createTestUser();
        List<Tag> tags = List.of(createTestTag());
        TagResponseDto responseDto = createTestTagResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagRepository.findAllByUser(user)).thenReturn(tags);
        when(tagMapper.toResponseDto(any(Tag.class))).thenReturn(responseDto);

        List<TagResponseDto> result = tagService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(tagRepository).findAllByUser(user);
        verify(tagMapper).toResponseDto(any(Tag.class));
    }

    @Test
    public void getById_ShouldReturnTag_WhenExists() {
        Long tagId = 1L;
        User user = createTestUser();
        Tag tag = createTestTag();
        TagResponseDto responseDto = createTestTagResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagRepository.findByIdAndUser(tagId, user)).thenReturn(Optional.of(tag));
        when(tagMapper.toResponseDto(tag)).thenReturn(responseDto);

        TagResponseDto result = tagService.getById(tagId);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(tagRepository).findByIdAndUser(tagId, user);
        verify(tagMapper).toResponseDto(tag);
    }

    @Test
    public void getById_ShouldThrowNotFoundException_WhenNotFound() {
        Long tagId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagRepository.findByIdAndUser(tagId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.getById(tagId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Tag not found");

        verify(authenticationService).getCurrentUser();
        verify(tagRepository).findByIdAndUser(tagId, user);
        verifyNoInteractions(tagMapper);
    }

    @Test
    public void create_ShouldCreateAndReturnTag_WhenValidInput() {
        User user = createTestUser();
        TagRequestDto requestDto = createTestTagRequestDto();
        Tag tag = createTestTag();
        Tag savedTag = createTestTag();
        TagResponseDto responseDto = createTestTagResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagMapper.toEntity(requestDto)).thenReturn(tag);
        when(tagRepository.save(tag)).thenReturn(savedTag);
        when(tagMapper.toResponseDto(savedTag)).thenReturn(responseDto);

        TagResponseDto result = tagService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(tagMapper).toEntity(requestDto);
        verify(tagRepository).save(tag);
        verify(tagMapper).toResponseDto(savedTag);
    }

    @Test
    public void update_ShouldUpdateAndReturnTag_WhenTagExists() {
        Long tagId = 1L;
        User user = createTestUser();
        TagRequestDto requestDto = createTestTagRequestDto();
        Tag tag = createTestTag();
        Tag updatedTag = createTestTag();
        TagResponseDto responseDto = createTestTagResponseDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagRepository.findByIdAndUser(tagId, user)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(updatedTag);
        when(tagMapper.toResponseDto(updatedTag)).thenReturn(responseDto);

        TagResponseDto result = tagService.update(tagId, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(authenticationService).getCurrentUser();
        verify(tagRepository).findByIdAndUser(tagId, user);
        verify(tagMapper).updateEntity(tag, requestDto);
        verify(tagRepository).save(tag);
        verify(tagMapper).toResponseDto(updatedTag);
    }

    @Test
    public void update_ShouldThrowNotFoundException_WhenTagNotFound() {
        Long tagId = 1L;
        User user = createTestUser();
        TagRequestDto requestDto = createTestTagRequestDto();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagRepository.findByIdAndUser(tagId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.update(tagId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Tag not found");

        verify(authenticationService).getCurrentUser();
        verify(tagRepository).findByIdAndUser(tagId, user);
        verifyNoInteractions(tagMapper);
    }

    @Test
    public void delete_ShouldDeleteTag_WhenTagExists() {
        Long tagId = 1L;
        User user = createTestUser();
        Tag tag = createTestTag();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagRepository.findByIdAndUser(tagId, user)).thenReturn(Optional.of(tag));

        tagService.delete(tagId);

        verify(authenticationService).getCurrentUser();
        verify(tagRepository).findByIdAndUser(tagId, user);
        verify(tagRepository).delete(tag);
    }

    @Test
    public void delete_ShouldThrowNotFoundException_WhenTagNotFound() {
        Long tagId = 1L;
        User user = createTestUser();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(tagRepository.findByIdAndUser(tagId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.delete(tagId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Tag not found");

        verify(authenticationService).getCurrentUser();
        verify(tagRepository).findByIdAndUser(tagId, user);
        verify(tagRepository, never()).delete(any());
    }
} 