package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.auth.converter.AuthConverter;
import com.project.team5backend.domain.auth.entity.Auth;
import com.project.team5backend.domain.auth.repository.AuthRepository;
import com.project.team5backend.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

    private final AuthRepository authRepository;

    @Override
    public void savePassword(User user, String encodedPassword) {
        Auth auth = AuthConverter.toAuth(user, encodedPassword);
        authRepository.save(auth);
    }
}
