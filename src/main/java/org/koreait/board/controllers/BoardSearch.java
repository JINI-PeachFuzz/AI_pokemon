package org.koreait.board.controllers;

import lombok.Data;
import org.koreait.global.paging.CommonSearch;

import java.util.List;

@Data
public class BoardSearch extends CommonSearch {
    private List<String> bid; // 기본은 게시판 한개한개를 조회하는게 보통
    private String sort; // 필드명_정렬방향 / 예) viewCount_DESC
    private List<String> email; // 회원 이메일 -> 내가 쓴 게시글만 보는 경우도 있으니까 추가했음
}
