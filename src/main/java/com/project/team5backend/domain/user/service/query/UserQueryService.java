package com.project.team5backend.domain.user.service.query;

import com.project.team5backend.domain.user.dto.response.UserResDTO;

public interface UserQueryService {
    UserResDTO.UserProfileResDTO getUserProfile(Long id);
}

