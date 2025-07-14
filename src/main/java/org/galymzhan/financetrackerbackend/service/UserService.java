package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.request.UserProfileUpdateDto;
import org.galymzhan.financetrackerbackend.dto.response.UserProfileResponseDto;

public interface UserService {

    UserProfileResponseDto getCurrentUserProfile();

    UserProfileResponseDto updateCurrentUserProfile(UserProfileUpdateDto userProfileUpdateDto);
}
