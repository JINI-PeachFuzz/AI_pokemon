package org.koreait.message.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;
import org.koreait.message.constants.MessageStatus;

import java.util.List;

@Data
@Entity
public class Message extends BaseEntity {
    @Id @GeneratedValue
    private Long seq;
    // 전체공지를 할 거임

    private boolean notice; //공지

    @Column(length = 45, nullable = false)
    private String gid; // 첨부파일도 보낼 수 있으니까! / 메일에 파일첨부도 같이~

    @Enumerated(EnumType.STRING) // 열람, 미열람
    @Column(length = 10, nullable = false)
    private MessageStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender")
    // 회원전용가능으로 관계매핑 / 한명이 여러명에게 보낼 수 있으니 매니투원
    private Member sender; // 보내는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver")
    private Member receiver; // 받는 사람 / 이메일을 받고 나중에 디비처리시에 값을 넣는걸로

    @Column(length = 150, nullable = false) // 필수이기 때문에 널안됌
    private String subject; // 쪽지 제목

    @Lob // 라지 오브젝트
    @Column(nullable = false)
    private String content; // 쪽지 내용

    @Transient
    private List<FileInfo> editorImages; // 2차가공을 통해서 content로 넣어줄거임

    @Transient
    private List<FileInfo> attachFiles; // 2차가공을 통해서 넣어줄거임

}