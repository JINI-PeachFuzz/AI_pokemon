package org.koreait.mypage.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends BaseEntity {

    @Id @GeneratedValue
    private Long seq;

    // 팔로잉 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="following")
    private Member following;

    // 팔로우 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="follower")
    private Member follower;
}