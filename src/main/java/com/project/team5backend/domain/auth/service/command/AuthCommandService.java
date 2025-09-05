package com.project.team5backend.domain.auth.service.command;

import com.project.team5backend.domain.user.entity.User;

public interface AuthCommandService {

    void savePassword(User user, String encodedPassword);
}
