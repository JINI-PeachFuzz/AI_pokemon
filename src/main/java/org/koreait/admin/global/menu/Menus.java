package org.koreait.admin.global.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menus {
    private static Map<String, List<MenuDetail>> submenus;//정적으로만 접근할 수 있게 할려고 스태틱사용
        // 지네릭으로 레코드 클래스인 MenuDetail를 넣었음

    static {
        submenus = new HashMap<>();

        // 기본 설정
        submenus.put("basic", List.of(
            new MenuDetail("siteConfig", "사이트 기본 정보", "/admin/basic/siteConfig"),
                new MenuDetail("terms", "약관 관리", "/admin/basic/terms") // 약관관리라고 해서 따로 뺄거 / 쇼핑몰같은데보면 약관이 많이 사용되기 때문
        ));

        // 회원관리
        submenus.put("member", List.of(
           new MenuDetail("list", "회원목록", "/admin/member/list")

        ));

        // 게시판관리
        submenus.put("board", List.of(
           new MenuDetail("list","게시판목록", "/admin/board/list"),
           new MenuDetail("add", "게시판등록", "/admin/board/add"),
           new MenuDetail("posts", "게시글관리", "/admin/board/posts")
        ));

    }

    public static List<MenuDetail> getMenus(String menuCode) {
        return submenus.get(menuCode); // 베이직, 멤버, 보드가 자동으로 노출이 되게 / 연동이 되게

    }

}