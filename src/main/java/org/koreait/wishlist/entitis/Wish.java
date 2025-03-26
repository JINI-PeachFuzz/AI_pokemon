package org.koreait.wishlist.entitis;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.member.entities.Member;
import org.koreait.wishlist.constants.WishType;
// import org.springframework.data.annotation.Id;

/**
 * 즐겨찾기 Entity
 *
 * 기본키(복합키) 3가지 조합
 * WishType(mode) + seq + 회원번호
 *
 * seq와 회원번호는 중복 가능성 있으므로
 *
 */
@Data
@Entity
@IdClass(WishId.class)
public class Wish {

    @Id
    private Long seq;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length=15, name="_type")
    private WishType type;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}