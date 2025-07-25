package org.galymzhan.financetrackerbackend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.request.UserProfileUpdateDto;
import org.galymzhan.financetrackerbackend.dto.response.UserProfileResponseDto;
import org.galymzhan.financetrackerbackend.entity.User;
import org.galymzhan.financetrackerbackend.mapper.UserMapper;
import org.galymzhan.financetrackerbackend.repository.UserRepository;
import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.galymzhan.financetrackerbackend.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileResponseDto getCurrentUserProfile() {
        User user = authenticationService.getCurrentUser();
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateCurrentUserProfile(UserProfileUpdateDto userProfileUpdateDto) {
        User user = authenticationService.getCurrentUser();
        userMapper.updateEntity(user, userProfileUpdateDto);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }
}
