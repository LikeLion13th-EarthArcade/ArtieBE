package com.project.team5backend.global.security.userdetails;

import com.project.team5backend.domain.auth.entity.Auth;
import com.project.team5backend.domain.auth.repository.AuthRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        log.info("[ CustomUserDetailsService ] UserId 를 이용하여 User 를 검색합니다.");

        // Worker 로그인
        Optional<User> workerOpt = userRepository.findByEmail(userId);
        if (workerOpt.isPresent()) {
            User user = workerOpt.get();
            Auth auth = authRepository.findByUser((user))
                    .orElseThrow(() -> new UsernameNotFoundException("개인 사용자 없음"));

            return new CustomUserDetails(user.getId(), user.getEmail(), auth.getPassword(), user.getRole());
        }

        // 아무것도 찾지 못했을 때
        throw new UsernameNotFoundException("사용자 없음");
    }
}
