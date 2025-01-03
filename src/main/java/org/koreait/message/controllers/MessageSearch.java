package org.koreait.message.controllers;

import lombok.Data;
import org.koreait.global.paging.CommonSearch;

import java.util.List;

@Data
public class MessageSearch extends CommonSearch {
    private List<String> sender; // 보낸쪽 이메일
    private String mode; // receiver이거나 값이 없으면 받은 쪽지, send : 보낸 쪽지만 가져오게 // 메시지를 볼때는 보내고 받는걸 같이 보니까 2가지를 구분해서 사용할 수 있게 추가했음
}
