package com.project.team5backend.domain.user.service.command;

import com.project.team5backend.domain.user.dto.request.UserReqDTO;

public interface UserCommandService {

    void createUser(UserReqDTO.UserCreateReqDTO userCreateReqDTO);

    void updateUser(long id, UserReqDTO.UserUpdateReqDTO userUpdateReqDTO);

    void withdrawalUser(long id, String accessToken, String refreshToken);
}