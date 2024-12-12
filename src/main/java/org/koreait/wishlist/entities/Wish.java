package org.koreait.wishlist.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.Length;
import org.koreait.member.entities.Member;
import org.koreait.wishlist.constants.WishType;

@Data
@Entity
@IdClass(WishId.class)
public class Wish {

    @Id
    private Long seq; // 이건 기본키는 아니고 조합해서 만들 꺼임

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length=15, name = "_type")
    private WishType type; // 예약어 , 테이블 피드명만 변경할 꺼임

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; // 비회원은 찜하기 못함

    // ID를 3개를 묶어서 복합키 형태로 만들었음

}
