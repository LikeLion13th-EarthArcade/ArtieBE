package com.project.team5backend.global.security.userdetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.team5backend.domain.user.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends CurrentUser implements UserDetails {

    //    private final String email;
    @JsonIgnore
    private String password;
//    private final String roles;

    // Worker 인증 객체 생성
    public CustomUserDetails(Long id, String email, String password, Role role) {
        super(id, email, role);
        this.password = password;
    }

//    // Corp 인증 객체 생성
//    public CustomUserDetails(Corp corp, CorpAuth corpAuth) {
//        super(corp.getId(), corp.getLoginId(), corp.getRole());
//        this.password = corpAuth.getPassword();
//    }

    // 권한을 불변 컬렉션으로 반환 (우리의 서비스는 단일 권한이라고 가정 (관리자 페이지 있으면 관리자 권한 추가))
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(super.getRole().name()));
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Account 가 만료되었는지?
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Account 가 잠겨있는지?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Credential 만료되지 않았는지?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 활성화가 되어있는지?
    @Override
    public boolean isEnabled() {
        // User Entity 에서 Status 가져온 후 true? false? 검사
        return true;
    }
}
