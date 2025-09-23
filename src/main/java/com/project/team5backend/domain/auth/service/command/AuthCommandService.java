package com.project.team5backend.domain.auth.service.command;

import com.project.team5backend.domain.auth.dto.request.AuthReqDTO;
import com.project.team5backend.domain.user.entity.User;

public interface AuthCommandService {

    void savePassword(User user, String encodedPassword);

    void changePassword(long id, AuthReqDTO.AuthPasswordChangeReqDTO authPasswordChangeReqDTO);

    String tempPassword(AuthReqDTO.AuthTempPasswordReqDTO authTempPasswordReqDTO);
}
