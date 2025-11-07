package com.project.team5backend.global.config;

import com.project.team5backend.global.security.exception.CustomAccessDeniedHandler;
import com.project.team5backend.global.security.exception.CustomAuthenticationEntryPoint;
import com.project.team5backend.global.security.filter.CustomLoginFilter;
import com.project.team5backend.global.security.filter.JwtAuthorizationFilter;
import com.project.team5backend.global.security.handler.CustomLogoutHandler;
import com.project.team5backend.global.security.handler.CustomLogoutSuccessHandler;
import com.project.team5backend.global.security.repository.CustomCookieCsrfTokenRepository;
import com.project.team5backend.global.security.util.JwtUtil;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration // 빈 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableMethodSecurity // 권한 별 접근 제한 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RedisUtils<String> redisUtils;
    private final CustomLogoutHandler jwtLogoutHandler;
    private final CustomLogoutSuccessHandler jwtLogoutSuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomCookieCsrfTokenRepository customCookieCsrfTokenRepository;
    private final @Qualifier("customCorsConfigurationSource") CorsConfigurationSource corsConfigurationSource;


    //인증이 필요하지 않은 url
    private final String[] allowUrl = {
            "/api/v1/auth/login",   //로그인
            "/api/v1/users",
            "/api/v1/security/reissue-cookie", // 쿠키 재발급
            "/api/v1/security/csrf",    // csrf 토큰 발급
            "/api/v1/validations/**",
            "/api/v1/auth/reset-password",
            "swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 로그인 필터 객체 생성
        CustomLoginFilter loginFilter = new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, customCookieCsrfTokenRepository);
        // 로그인 앤드 포인트
        loginFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
                // CORS CONFIG
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 요쳥별 접근 권한 설정
                .authorizeHttpRequests(request -> request
                        // 허용할 Url은 인증 없이 접근 허용
                        .requestMatchers(allowUrl).permitAll()
                        // 그 외 모든 요청에 대해서 인증
                        .anyRequest().authenticated())

                // JWT 인증 필터 등록
                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, redisUtils), UsernamePasswordAuthenticationFilter.class)

                // 커스텀 로그인 필터 등록 -> 기본 폼 로그인 대신 JWT 로직 실행
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)

                // SPRING SECURITY 기본 로그인 폼 비활성화 -> REST API 기반 JWT 사용
                .formLogin(AbstractHttpConfigurer::disable)

                // HTTP BASIC 인증 비활성화 -> JWT은 사용하지 않음
                .httpBasic(HttpBasicConfigurer::disable)

                // JSESSIONID 비활성화
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // TODO : CSRF 토큰 + SameSite + Origin 검증
                // CSRF 설정 (JWT를 헤더에 넣으면 필요 없는데 쿠키는 해야함)
                // CSRF: 쿠키<->헤더 일치(Double Submit Cookie)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(customCookieCsrfTokenRepository)
                        // 로그인/회원가입/문서 등 최소 범위만 예외. 이후 프론트가 헤더 붙이면 예외 줄여도 됨.
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**",
                                "/actuator/**"
                        )
                )
//                .csrf(AbstractHttpConfigurer::disable)

                // logout
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(jwtLogoutSuccessHandler)
                )

                // 예외 처리 핸들러 설정
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // 인증 자체가 안 된 경우 (401)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        // 인증은 되었지만 권한이 없을 때 (403)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
