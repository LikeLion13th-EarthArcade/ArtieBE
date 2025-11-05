package com.project.team5backend.domain.user.service.query;


import com.project.team5backend.domain.user.converter.UserConverter;
import com.project.team5backend.domain.user.dto.response.UserResDTO;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public UserResDTO.UserProfileResDTO getUserProfile(long id) {
        User user = getUser(id);

        return UserConverter.toUserProfileResDTO(user);
    }

    private User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}

