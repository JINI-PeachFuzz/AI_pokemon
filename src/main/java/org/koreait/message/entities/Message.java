package org.koreait.message.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;
import org.koreait.message.constants.MessageStatus;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor // 편법으로 기본생성자가 프라이빗으로 생성되기 때문에 추가한거
@Table(indexes = @Index(name="idx_notice_created_at", columnList = "notice DESC, createdAt DESC")) // 공지사항을 인덱스번호로 좀 더 빨리 조회할 수 있게 하기 위해 추가한거
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
    private List<FileInfo> editorImages; // 2차가공을 통해서 content로 넣어줄거임 / MessageInfoService

    @Transient
    private List<FileInfo> attachFiles; // 2차가공을 통해서 넣어줄거임 / MessageInfoService

    @Transient
    private boolean received; // 2차가공으로 쪽지조회시 사용예정

    @Transient
    private boolean deletable; // 삭제 가능 여부

    private boolean deletedBySender; // 보내는 쪽에서 쪽지를 삭제한 경우

    private boolean deletedByReceiver; // 받는 쪽에서 쪽지를 삭제한 경우

}
