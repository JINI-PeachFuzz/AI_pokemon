package org.koreait.member.libs;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Member;
import org.koreait.member.services.MemberInfoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Lazy // 순환참조발생도 방지하는거 같기도 함 / 멤버인포서비스에도 넣어줬음
@Setter
@Component
public class MemberUtil {

    private Member member; // 있으면 이걸로 하고 없으면 가져오는 걸로 (하단참고)

    public boolean isLogin() {
        return getMember() != null;
    }

    /*
    * 관리자 여부
    * 권한 - MANAGER, ADMIN
    * */
    public boolean isAdmin(){
        /*
        return isLogin() &&
                getMember().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority() == Authority.ADMIN || a.getAuthority() == Authority.MANAGER);

         */
        return false;
    }


    /**
     * 로그인 한 회원의 정보 조회
     *
     * @return
     */
    public Member getMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof MemberInfo memberInfo) {
            return member== null ? memberInfo.getMember() : member;
        } // null일땐 새로

        return null;
    }
}