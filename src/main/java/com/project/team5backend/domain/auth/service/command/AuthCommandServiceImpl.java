package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.auth.converter.AuthConverter;
import com.project.team5backend.domain.auth.dto.request.AuthReqDTO;
import com.project.team5backend.domain.auth.entity.Auth;
import com.project.team5backend.domain.auth.exception.AuthErrorCode;
import com.project.team5backend.domain.auth.exception.AuthException;
import com.project.team5backend.domain.auth.repository.AuthRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.mail.service.MailService;
import com.project.team5backend.global.util.RedisUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.project.team5backend.global.constant.redis.RedisConstant.KEY_SCOPE_SUFFIX;
import static com.project.team5backend.global.constant.scope.ScopeConstant.SCOPE_RESET_PASSWORD;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisUtils<String> redisUtils;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    public void savePassword(User user, String encodedPassword) {
        Auth auth = AuthConverter.toAuth(user, encodedPassword);
        authRepository.save(auth);
    }

    @Override
    public void changePassword(long id, AuthReqDTO.AuthPasswordChangeReqDTO authPasswordChangeReqDTO) {
        // 새 비밀번호와 비밀번호 입력 확인이 다를 경우
        if (!authPasswordChangeReqDTO.newPassword().equals(authPasswordChangeReqDTO.newPasswordConfirmation())) {
            throw new AuthException(AuthErrorCode.NEW_PASSWORD_DOES_NOT_MATCH);
        }

        // Auth 객체 찾기
        Auth auth = authRepository.findByUserId(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.AUTH_NOT_FOUND));

        // 현재 비밀번호가 틀렸을 때
        if (!passwordEncoder.matches(authPasswordChangeReqDTO.currentPassword(), auth.getPassword())) {
            throw new AuthException(AuthErrorCode.CURRENT_PASSWORD_DOES_NOT_MATCH);
        }

        // 기존의 비밀번호와 새 비밀번호가 동일할 때
        if (passwordEncoder.matches(authPasswordChangeReqDTO.newPassword(), auth.getPassword())) {
            throw new AuthException(AuthErrorCode.NEW_PASSWORD_IS_CURRENT_PASSWORD);
        }

        // 영속 상태인 객체 에서 auth를 가져왔으므로 auth도 영속 더티 채킹 O
        auth.updatePassword(passwordEncoder.encode(authPasswordChangeReqDTO.newPassword()));
    }

    @Override
    public void resetPassword(AuthReqDTO.AuthResetPasswordReqDTO authResetPasswordReqDTO) {

        // 이메일 인증이 있는지 확인
        final String email = authResetPasswordReqDTO.email();
        // 해당 인증이 비밀번호 재설정을 위한 것인지 확인
        if (!Objects.equals(redisUtils.get(email + KEY_SCOPE_SUFFIX), SCOPE_RESET_PASSWORD)) {
            throw new AuthException(AuthErrorCode.EMAIL_VALIDATION_DOES_NOT_EXIST);
        }

        // 새 비밀번호와 비밀번호 입력 확인이 다를 경우
        if (!authResetPasswordReqDTO.newPassword().equals(authResetPasswordReqDTO.newPasswordConfirmation())) {
            throw new AuthException(AuthErrorCode.NEW_PASSWORD_DOES_NOT_MATCH);
        }

        // Auth 객체 찾기
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.AUTH_NOT_FOUND));

        // 기존의 비밀번호와 새 비밀번호가 동일할 때
        if (passwordEncoder.matches(authResetPasswordReqDTO.newPassword(), auth.getPassword())) {
            throw new AuthException(AuthErrorCode.NEW_PASSWORD_IS_CURRENT_PASSWORD);
        }

        // 영속 상태인 객체 에서 auth를 가져왔으므로 auth도 영속 더티 채킹 O
        auth.updatePassword(passwordEncoder.encode(authResetPasswordReqDTO.newPassword()));

        // 인증 정보 삭제
        redisUtils.delete(email + KEY_SCOPE_SUFFIX);
    }

//    private String createTempPassword() {
//        SecureRandom random = new SecureRandom();
//        StringBuilder tempPassword = new StringBuilder();
//
//        List<Character> charPassword = new ArrayList<>();
//
//        while(charPassword.size() < PASSWORD_SIZE) {
//            charPassword.add(randomChar(LETTERS));
//            charPassword.add(randomChar(DIGITS));
//        }
//        charPassword.add(randomChar(SPECIALS));
//
//        Collections.shuffle(charPassword, random);
//
//        for (Character c : charPassword) {
//            tempPassword.append(c);
//        }
//        return tempPassword.toString();
//    }
//
//    private char randomChar(String pool) {
//        SecureRandom random = new SecureRandom();
//        return pool.charAt(random.nextInt(pool.length()));
//    }
}
