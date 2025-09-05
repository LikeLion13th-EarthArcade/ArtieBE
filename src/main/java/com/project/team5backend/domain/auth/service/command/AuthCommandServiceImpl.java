package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.user.converter.UserConverter;
import com.project.team5backend.domain.user.dto.request.UserReqDTO;
import com.project.team5backend.domain.user.dto.response.UserResDTO;
import com.project.team5backend.domain.user.entity.Role;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.Duration;
import java.util.Random;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

}
