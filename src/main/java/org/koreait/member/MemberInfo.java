package org.koreait.member;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.koreait.member.entities.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Builder
@ToString
public class MemberInfo implements UserDetails {

    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 비번만료관련
    public boolean isCredentialsNonExpired() {
        LocalDateTime credentialChangedAt = member.getCredentialChangedAt();
        return credentialChangedAt != null && // 지난 변경시점에서 값이 널이 아니고 30일이 안됐을경우 기본페이지로 넘어가게
                credentialChangedAt.isAfter(LocalDateTime.now().minusMonths(1L)); // 한달기준으로 인증
    }

    @Override
    public boolean isEnabled() { // 회원 탈퇴 여부
        return member.getDeletedAt() == null;

    }
}
// 위에 isAccountNonExpired, isAccountNonLocked 등 안에 내용없는것들도
// 시큐리티에서 참이냐 거짓이냐에 따라서 이미 판별하는데
// 값이 false이면 로그인이 안되는거임