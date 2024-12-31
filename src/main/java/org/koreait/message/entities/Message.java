package org.koreait.message.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;

@Data
@Entity
public class Message extends BaseEntity {
    @Id @GeneratedValue
    private Long seq;
    // 전체공지를 할 거임

    private boolean notice; //공지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender")
    // 회원전용가능으로 관계매핑 / 한명이 여러명에게 보낼 수 있으니 매니투원
    private Member sender; // 보내는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver")
    private Member receiver; // 받는 사람

    @Column(length = 150, nullable = false) // 필수이기 때문에 널안됌
    private String subject; // 쪽지 제목

    @Lob // 라지 오브젝트
    @Column(nullable = false)
    private String content; // 쪽지 내용
}
