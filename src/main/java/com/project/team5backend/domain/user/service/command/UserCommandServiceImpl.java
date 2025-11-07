package com.project.team5backend.domain.user.service.command;

import com.project.team5backend.domain.auth.service.command.AuthCommandService;
import com.project.team5backend.domain.user.converter.UserConverter;
import com.project.team5backend.domain.user.dto.request.UserReqDTO;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.security.util.JwtUtil;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.project.team5backend.global.constant.redis.RedisConstant.KEY_SCOPE_SUFFIX;
import static com.project.team5backend.global.constant.scope.ScopeConstant.SCOPE_SIGNUP;
import static com.project.team5backend.global.util.UpdateUtils.updateIfChanged;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthCommandService authCommandService;
    private final JwtUtil jwtUtil;
    private final RedisUtils<String> redisUtils;

    @Override
    public void createUser(UserReqDTO.UserCreateReqDTO userCreateReqDTO) {
        // 이메일 인증이 완료되었는지 확인
        // 해당 이메일 인증이 회원 가입을 위한 것인지 확인 & 비밀번호 검증
       signUpValidation(userCreateReqDTO);
        User user = UserConverter.toUser(userCreateReqDTO);
        try {
            userRepository.save(user);
            authCommandService.savePassword(user, encodePassword(userCreateReqDTO.password()));
        } catch (DataIntegrityViolationException e) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATED);
        }

        // 가입 성공 시 인증 정보 삭제
        redisUtils.delete(userCreateReqDTO.email() + KEY_SCOPE_SUFFIX);
    }

    @Override
    public void updateUser(Long id, UserReqDTO.UserUpdateReqDTO userUpdateReqDTO) {
        User user = getUser(id);

        updateIfChanged(userUpdateReqDTO.name(), user.getName(), user::updateName);
    }

    @Override
    public void withdrawalUser(Long id, String accessToken, String refreshToken) {
        User user = getUser(id);
        user.delete();
        jwtUtil.saveBlackListToken(user.getEmail(), accessToken, refreshToken);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private void signUpValidation(UserReqDTO.UserCreateReqDTO userCreateReqDTO) {
        if (!Objects.equals(redisUtils.get(userCreateReqDTO.email() + KEY_SCOPE_SUFFIX), SCOPE_SIGNUP)) {
            throw new UserException(UserErrorCode.SIGN_UP_EMAIL_VALIDATION_DOES_NOT_EXIST);
        }
        // 비밀번호 확인 점검 (근데 이걸 굳이 백엔드가?)
        if (!Objects.equals(userCreateReqDTO.password(), userCreateReqDTO.passwordConfirmation())) {
            throw new UserException(UserErrorCode.WRONG_PASSWORD_CONFIRMATION);
        }
    }
}