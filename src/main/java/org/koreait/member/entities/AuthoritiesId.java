package org.koreait.member.entities;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.koreait.member.constants.Authority;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자
public class AuthoritiesId {
    private Member member;
    private Authority authority;
}
