package com.project.team5backend.domain.user.service.command;

import com.project.team5backend.domain.auth.service.command.AuthCommandService;
import com.project.team5backend.domain.user.converter.UserConverter;
import com.project.team5backend.domain.user.dto.request.UserReqDTO;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.team5backend.global.util.UpdateUtils.updateIfChanged;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthCommandService authCommandService;
    private final JwtUtil jwtUtil;

    @Override
    public void createUser(UserReqDTO.UserCreateReqDTO userCreateReqDTO) {
        User user = UserConverter.toUser(userCreateReqDTO);
        try {
            userRepository.save(user);
            authCommandService.savePassword(user, encodePassword(userCreateReqDTO.password()));
        } catch (DataIntegrityViolationException e) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATED);
        }
    }

    @Override
    public void updateUser(long id, UserReqDTO.UserUpdateReqDTO userUpdateReqDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        updateIfChanged(userUpdateReqDTO.name(), user.getName(), user::updateName);
    }

    @Override
    public void withdrawalUser(long id, String accessToken, String refreshToken) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.delete();
        jwtUtil.saveBlackListToken(user.getEmail(), accessToken, refreshToken);
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}