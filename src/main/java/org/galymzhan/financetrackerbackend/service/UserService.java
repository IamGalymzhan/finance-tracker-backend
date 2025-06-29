package org.galymzhan.financetrackerbackend.service;

import org.galymzhan.financetrackerbackend.dto.UserProfileResponseDto;
import org.galymzhan.financetrackerbackend.dto.UserProfileUpdateDto;
import org.galymzhan.financetrackerbackend.exceptions.NotFoundException;

public interface UserService {

    UserProfileResponseDto getCurrentUserProfile();

    UserProfileResponseDto updateCurrentUserProfile(UserProfileUpdateDto userProfileUpdateDto) throws NotFoundException;
}
