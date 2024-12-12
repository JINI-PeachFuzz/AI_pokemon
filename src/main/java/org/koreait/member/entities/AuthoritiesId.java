package org.koreait.member.entities;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.koreait.member.constants.Authority;

@EqualsAndHashCode // equals()와 hashCode() 메서드를 자동 생성.
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자를 자동 생성.
@NoArgsConstructor // 기본 생성자 / 매개변수가 없는 기본 생성자를 자동으로 생성
public class AuthoritiesId {
    private Member member;
    private Authority authority;
}
