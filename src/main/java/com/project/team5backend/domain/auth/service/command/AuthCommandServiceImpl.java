package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.auth.converter.AuthConverter;
import com.project.team5backend.domain.auth.dto.request.AuthReqDTO;
import com.project.team5backend.domain.auth.entity.Auth;
import com.project.team5backend.domain.auth.exception.AuthErrorCode;
import com.project.team5backend.domain.auth.exception.AuthException;
import com.project.team5backend.domain.auth.repository.AuthRepository;
import com.project.team5backend.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
}
