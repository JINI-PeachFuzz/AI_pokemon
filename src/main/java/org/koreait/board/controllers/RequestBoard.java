package org.koreait.board.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.koreait.file.entities.FileInfo;

import java.util.List;

// 보드컨트롤쪽에 있는 커맨드 객체임
@Data
public class RequestBoard {


    private Long seq; // 게시글 번호 // 수정할때는 필수체크로 넣어줘야함
    private String mode; // 수정할때는 필수체크로 넣어줘야함

    @NotBlank
    private String bid; // 게시판 아이디

    @NotBlank
    private String gid;

    @NotBlank
    private String poster; // 작성자

    @Size(min=4)
    private String guestPw; // 비회원 비밀번호

    @NotBlank
    private String subject; // 글 제목

    @NotBlank
    private String content; // 글 내용
    private boolean notice; // 공지글 여부 // 게시글 양식, 규칙같은걸 가장 상단에 배치하는 거

    private String externalLink; // 외부링크
    private String youtubeUrl; // Youtube 주소

    private String category; // 게시글 분류

    private List<FileInfo> editorImages;
    private List<FileInfo> attachFiles;

}
