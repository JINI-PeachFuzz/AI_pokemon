package org.koreait.wishlist.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.wishlist.constants.WishType;

// 기본키로 사용할 거니 동률성에 대한 비교도 필요하고 기본생성자도 있어야함

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WishId {
    private Long seq;
    private WishType type;
    private Member member;

}
