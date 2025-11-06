package com.project.team5backend.global.validation.service;

import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.biznumber.client.BizNumberClient;
import com.project.team5backend.global.biznumber.dto.response.BizNumberResDTO;
import com.project.team5backend.global.mail.MailType;
import com.project.team5backend.global.mail.service.MailService;
import com.project.team5backend.global.util.RedisUtils;
import com.project.team5backend.global.validation.converter.ValidationConverter;
import com.project.team5backend.global.validation.dto.request.ValidationReqDTO;
import com.project.team5backend.global.validation.dto.response.ValidationResDTO;
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
import static com.project.team5backend.global.constant.scope.ScopeConstant.SCOPE_BIZ_NUMBER;
import static com.project.team5backend.global.constant.valid.MessageConstant.BIZ_NUMBER_IS_NOT_REGISTERED;
import static com.project.team5backend.global.constant.valid.MessageConstant.BLANK;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ValidationServiceImpl implements ValidationService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final RedisUtils<String> redisUtils;
    private final BizNumberClient bizNumberClient;

    @Override
    public String sendCode(MailType mailType, String scope, ValidationReqDTO.EmailCodeReqDTO emailCodeReqDTO) {
        // 이메일을 가져온다
        final String email = emailCodeReqDTO.email();
        // 6자리 난수 코드
        final String code = createVerificationCode();
        boolean isEmailAlreadyExist = userRepository.findByEmail(email).isPresent() || redisUtils.hasKey(email + KEY_CODE_SUFFIX);
        boolean isEmailVerification = Objects.equals(mailType, MailType.SIGNUP_VERIFICATION);
        // 회원 가입 이메일 인증인데, 이미 존재하는 이메일이거나, 누군가 인증 확인을 받아 가입을 진행중인 이메일로 인증을 한 경우
        if (isEmailAlreadyExist && isEmailVerification) {
            throw new ValidationException(ValidationErrorCode.ALREADY_USED_EMAIL);
        }

        boolean isResetPasswordVerification = Objects.equals(mailType, MailType.RESET_PASSWORD_VERIFICATION);
        boolean emailExists = userRepository.findByEmail(email).isPresent();
        // 비밀번호 찾기 이메일 인증인데, 해당 이메일로 가입된 계정이 없는 경우
        if (isResetPasswordVerification && !emailExists) {
            throw new ValidationException(ValidationErrorCode.ACCOUNT_NOT_FOUND);
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

    @Override
    public void verifyCode(ValidationReqDTO.EmailCodeValidationReqDTO emailCodeValidationReqDTO) {
        // 이메일
        final String email = emailCodeValidationReqDTO.email();
        // 사용자가 입력한 숫자 코드
        final String code =  emailCodeValidationReqDTO.code();

        // 레디스에 저장된 인증 코드 + 사용 스코프 ( :으로 구별되어 있음 )
        final String value = redisUtils.get(email + KEY_CODE_SUFFIX);
        // 없으면 인증 정보가 없는 것
        if (value == null) {
            throw new ValidationException(ValidationErrorCode.VALIDATION_REQUEST_DOES_NOT_EXIST);
        }

        String[] values = value.split(":");
        final String storedCode = values[0];
        final String scope = values[1];

        // 비교
        if (!Objects.equals(code, storedCode)) {
            throw new ValidationException(ValidationErrorCode.VALIDATION_REQUEST_DOES_NOT_EXIST);
        }
        // 성공시 회원 가입을 위해 삭제
        redisUtils.delete(email + KEY_CODE_SUFFIX);
        redisUtils.delete(email + KEY_COOLDOWN_SUFFIX);

        // 해당 스코프에서 사용할 인증이 완료되었음을 레디스에 저장
        redisUtils.save(email + KEY_SCOPE_SUFFIX, scope, SCOPE_EXP_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public ValidationResDTO.BizNumberValidationResDTO verifyBizNumber(ValidationReqDTO.BizNumberValidationReqDTO bizNumberValidationReqDTO) {
        BizNumberResDTO.BizInfo.InfoItem infoItem = bizNumberClient.verifyBizNumber(bizNumberValidationReqDTO.bizNumber()).getInfoItem();
        ValidationResDTO.BizNumberValidationResDTO.Info info = ValidationConverter.toInfo(infoItem);
        boolean isValid = !Objects.equals(info.taxType(), BIZ_NUMBER_IS_NOT_REGISTERED);
        boolean isExpired = !Objects.equals(info.endAt(), BLANK);

        // 성공시 성공 정보를 redis에 저장
        if (isValid && !isExpired) {
            String bizNumber = bizNumberValidationReqDTO.bizNumber();
            redisUtils.save(bizNumber + KEY_SCOPE_SUFFIX, SCOPE_BIZ_NUMBER, SCOPE_EXP_TIME, TimeUnit.MILLISECONDS);
        }

        return ValidationConverter.toBizNumberValidationResDTO(info, isValid, isExpired);
    }

    // 6자리 난수 생성기
    private String createVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }
}
