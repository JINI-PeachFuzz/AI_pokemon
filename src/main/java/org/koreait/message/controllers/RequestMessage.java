package org.koreait.message.controllers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.koreait.file.entities.FileInfo;

import java.util.List;

@Data
public class RequestMessage {
    private boolean notice;
    /***
     * 메일을 받는 쪽 이메일
     *     필수가 되는 조건 : 회원이 다른 회원에게 쪽지를 보내는 경우
     *     필수가 아닌 조건 : 관리자가 공지사항(notice)으로 쪽지를 보내는 경우 / 이건 전체를 얘기함
     */
    @Email // 형식에 대한건 필요하니까
    private String email;

    @NotBlank
    private String gid; // 이건 따로 설정안하면 기본값을 넣어줄거임

    @NotBlank
    private String subject;

    @NotBlank
    private String content;


    private List<FileInfo> editorImages;
    private List<FileInfo> attachFiles;
}
