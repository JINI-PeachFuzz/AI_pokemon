package org.koreait.member.social.entities;

import lombok.Data;

@Data
public class SocialConfig { // 현재는 카카오만 있는데 추후 네이버도 여기에 추가하면 됨
    private boolean useKakaoLogin; // 카카오 로그인 사용 여부
    private String kakaoRestApiKey;

}
