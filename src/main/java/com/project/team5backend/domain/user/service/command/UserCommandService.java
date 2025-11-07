package com.project.team5backend.domain.user.service.command;

import com.project.team5backend.domain.user.dto.request.UserReqDTO;

public interface UserCommandService {

    void createUser(UserReqDTO.UserCreateReqDTO userCreateReqDTO);

    void updateUser(Long id, UserReqDTO.UserUpdateReqDTO userUpdateReqDTO);

    void withdrawalUser(Long id, String accessToken, String refreshToken);
}