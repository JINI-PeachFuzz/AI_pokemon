package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

// 감독자와 관련 / 엔티티베이스멤버 엔티티와 관련
@Lazy
@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {
    //AuditorAware<String> 이거는 시큐리티와 만나면 시너지가 올라감

    private final MemberUtil memberUtil;

    @Override
    public Optional<String> getCurrentAuditor() {
        String email = null; // 기본값
        if(memberUtil.isLogin()) {
            email = memberUtil.getMember().getEmail();
        }

        return Optional.ofNullable(email); // of만 하면 null일때 오류발생함
    }
}
