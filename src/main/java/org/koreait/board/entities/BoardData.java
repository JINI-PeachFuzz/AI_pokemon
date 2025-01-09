package org.koreait.board.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_bd_created_at", columnList = "createdAt DESC"), // createdAt는 필드명임
        @Index(name = "idx_bd_notice_created_at", columnList = "notice DESC, createdAt DESC")
})
public class BoardData extends BaseEntity implements Serializable {
    @Id @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; // 회원이 쓴 게시글도 있으니까

    @Column(length = 45, nullable = false)
    private String gid;

    @Column(length = 45, nullable = false)
    private String poster; // 작성자명

    @Column(length = 65)
    private String guestPw; // 글 수정, 삭제 비밀번호 // 해시화시킬꺼
    // 비회원일때만 필수기 때문에 nullable은 넣으면 안됨.

    private boolean notice; // 공지글 여부

    @Column(nullable = false)
    private String subject; // 글 제목

    @Lob
    private String content; // 필수는 아님 / 외부링크만 넣는 경우도 있으니까

    private long viewCount; // 조회수
    // 조회수 long으로 하거나 int로 하거나 ... max가 어느정도일지 보고..

    private long commentCount; // 댓글수

    @Column(length = 20)
    private String ipAddr; // ip 주소 // 차단하는기능도 있음.. // 한다면 환경변수로 추가해야될수도..?

    private String userAgent; // 브라우저 정보 // 예전엔 안드로이드인지 아이폰인지에 따라서 옆에 아이콘을 붙였던 적도 있었..

    @Column(length = 150)
    private String externalLink; // 외부 링크 -> 게시글 링크를 외부 링크로 변경 // 특정 url로 넘겨줘도 좋을 듯.

    @Column(length = 60)
    private String youtubeUrl; // 유튜브 영상 링크

    @Column(length = 60)
    private String category; // 게시글 분류

    @Transient
    private BoardData prev; // 이전 게시글

    @Transient
    private BoardData next; // 다음 게시글

    @Transient
    private List<FileInfo> editorImages; // 에디터 첨부 이미지

    @Transient
    private List<FileInfo> attachFiles; // 다운로드용 첨부 파일

}
