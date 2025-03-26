package org.koreait.wishlist.entitis;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.wishlist.constants.WishType;

/**
 * ID Class
 *
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WishId {

    private Long seq;

    private WishType type;

    private Member member;
}