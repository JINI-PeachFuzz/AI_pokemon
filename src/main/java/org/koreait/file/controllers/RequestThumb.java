package org.koreait.file.controllers;

import lombok.Data;

@Data
public class RequestThumb {
    private Long seq;
    private String url; // 원격 이미지 URL / Long값의 seq이든 url이든 둘중에 하나는 있어야함ㅁ
    private int width;
    private int height; // 너비와 높이를 정확하게 하는게 아니고 둘중에 큰걸 기준으로 삼음

}
