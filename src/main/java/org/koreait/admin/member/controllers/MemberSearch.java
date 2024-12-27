package org.koreait.admin.member.controllers;

import lombok.Data;
import org.koreait.global.paging.CommonSearch;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Authorities;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class MemberSearch extends CommonSearch {
    private List<String> email; // 한명만 조회가능 뿐만 아니라 여러명도 조회가능하게 리스트형으로 했음
    private List<Authority> authority;
    private String dateType; // 이게 없으면 가입일자로 / 아래 s와 e에 대한 기준이 없기 때문에 추가한거!, 가입일자라던지...

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eDate;

}
