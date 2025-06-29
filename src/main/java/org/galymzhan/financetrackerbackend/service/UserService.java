package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.UserProfileResponseDto;
import org.galymzhan.financetrackerbackend.dto.UserProfileUpdateDto;

public interface UserService {

    UserProfileResponseDto getCurrentUserProfile();

    UserProfileResponseDto updateCurrentUserProfile(UserProfileUpdateDto userProfileUpdateDto);
}
