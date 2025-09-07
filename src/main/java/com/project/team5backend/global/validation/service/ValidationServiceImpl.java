package com.project.team5backend.global.validation.service;

import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.mail.MailType;
import com.project.team5backend.global.mail.service.MailService;
import com.project.team5backend.global.util.RedisUtils;
import com.project.team5backend.global.validation.dto.request.ValidationReqDTO;
import com.project.team5backend.global.validation.exception.ValidationErrorCode;
import com.project.team5backend.global.validation.exception.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.project.team5backend.global.constant.redis.RedisConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ValidationServiceImpl implements ValidationService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final RedisUtils<String> redisUtils;

    @Override
    public String sendCode(MailType mailType, String scope, ValidationReqDTO.EmailCodeReqDTO emailCodeReqDTO) {
        // 이메일을 가져온다
        final String email = emailCodeReqDTO.email();
        // 6자리 난수 코드
        final String code = createVerificationCode();

        boolean isEmailAlreadyExist = userRepository.findByEmail(email).isPresent();
        boolean isEmailVerification = Objects.equals(mailType, MailType.SIGNUP_VERIFICATION);
        // 회원 가입 이메일 인증인데, 이미 존재하는 이메일로 인증을 한 경우
        if (isEmailAlreadyExist && isEmailVerification) {
            throw new ValidationException(ValidationErrorCode.ALREADY_USED_EMAIL);
        }

        // 해당 이메일로 인증 번호를 보낸 적이 있다면 10초 대기
        if (redisUtils.hasKey(email + KEY_COOLDOWN_SUFFIX)) {
            throw new ValidationException(ValidationErrorCode.CODE_COOL_DOWN);
        }

        Map<String, String> mailContent = new HashMap<>();
        mailContent.put("{{CODE}}", code);
        mailContent.put("{{TTL_MINUTES}}", "5");
        mailService.sendMail(mailType, email, mailContent);

        // 성공 시
        // 레디스에 인증 정보 저장
        redisUtils.save(email + KEY_CODE_SUFFIX, code + ":" + scope, CODE_EXP_TIME, TimeUnit.MILLISECONDS);
        // 쿨다운 키 저장 (연속 인증 방지)
        redisUtils.save(email + KEY_COOLDOWN_SUFFIX, VALUE_COOLDOWN, COOLDOWN_EXP_TIME, TimeUnit.MILLISECONDS);
        return code;
    }

    // 6자리 난수 생성기
    private String createVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }
}
