package org.koreait.global.paging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page 에 필요한 기초 Data 생성해주는 편의 객체
 *
 */

@Getter
@ToString
public class Pagination {

    private int page;
    private int total;
    private int ranges;
    private int limit;

    private int totalPages; // 전체 페이지
    private int firstRangePage; // 현재 구간에서 시작 Page 번호
    private int lastRangePage; // 현재 구간에서 종료 Page 번호
    private int prevRangeLastPage; // 이전 구간 종료 Page
    private int nextRangeFirstPage; // 다음 구간 시작 Page


    private String baseUrl;

    /***
     * 기본생성자
     *
     * @param page : 현재 페이지 번호 // -1 이라던지 들어오면 안됌, 그래서 기본값처리함
     * @param total : 총 Page(레코드) 개수
     * @param ranges : 페이지 구간 갯수
     * @param limit : 한 페이지당 출력될 레코드 갯수
     *
     * @param request : 검색 결과 Page 일 경우 queryString 가져오기 위함 // 이건 지웠음 그냥 참고 // 주소에 보면 ? 뒤에 검색데이터가 있는데 그건 페이지를 이동해도 남아있어야
     * 검색이 유지되는걸 구현 할 때 사용
     * 앞에 page, total은 필수임
     */

    public Pagination(int page, int total, int ranges, int limit) {
        this(page, total, ranges, limit, null);
    }
// 생성자 오버로드

    public Pagination(int page, int total, int ranges, int limit, HttpServletRequest request) {
        // 페이징 기본값 처리
        page = Math.max(page, 1);
        total = Math.max(total, 0);
        ranges = ranges < 1 ? 10 : ranges;
        limit = limit < 1 ? 20 : limit;

        if (total == 0) {
            return;
        }

        /**
         * 전체 Page 처리
         *
         * 올림처리 위해 한쪽 실수로 변경
         *
         * EX) 마지막 Page는 글(레코드) 개수가 Limit 보다 작아도
         *     1 Page 를 할당하기때문에
         *     올림처리 필수적
         */


        // 전체 페이지 갯수
        int totalPages = (int)Math.ceil(total / (double)limit);

        // 구간 번호 - 0, 1, 2
        // int 형태로 연산하기때문에 소수점 자동 버림처리
        int rangeCnt = (page -1) / ranges; // 어떤 구간인지를 알 수 있음 / 패턴구했던거 생각!
        int firstRangePage = rangeCnt * ranges +1;// 현재 구간의 시작 페이지 번호
        int lastRangePage = firstRangePage + ranges -1; // 현재 구간의 마지막 페이지 번호
        // lastRangePage = lastRangePage > totalPages ? totalPages : lastRangePage;
        lastRangePage = Math.min(lastRangePage, totalPages); // 마지막 구간의 마지막 Page 번호 처리, 둘 중 작은 것으로 대입

        int prevRangeFirstPage = 0, nextRangeFirstPage = 0; // 이전 구간 시작 페이지 번호, 다음 구간 시작 페이지 번호 // 값이 0이 아닐 경우 버튼 노출

        // 2번째 구간 이상 마지막 구간 미만일 경우에만 이전구간버튼 노출
        if (rangeCnt > 0) {
            prevRangeLastPage = firstRangePage - 1;
        }

        // 마지막 페이지 구간
        int lastRangeCnt = (totalPages - 1) / ranges;

        // 마지막 구간 이전까지만 다음구간버튼 노출
        if (rangeCnt < lastRangeCnt) {
            nextRangeFirstPage = (rangeCnt + 1) * ranges + 1;
        }


        /* 쿼리스트링 값 처리 S */
        String qs = request.getQueryString();
        baseUrl = "?";
        if (StringUtils.hasText(qs)) {

            // "?page=" 제거(필터)하고 다시 모아서 가공
            baseUrl += Arrays.stream(qs.split("&")) // A += B는 A = A + B와 같은 의미, 기존 문자열에 새로운 문자열을 이어붙이는 역할
                    .filter(s -> !s.contains("page="))
                    .collect(Collectors.joining("&")) + "&";
        }
        baseUrl += "page=";
        /* 쿼리스트링 값 처리 E */

        this.page = page;
        this.ranges = ranges;
        this.limit = limit;
        this.total = total;
        this.totalPages = totalPages;
        this.firstRangePage = firstRangePage;
        this.lastRangePage = lastRangePage;
        this.prevRangeLastPage = prevRangeLastPage;
        this.nextRangeFirstPage = nextRangeFirstPage;
    }

    /**
     * String 배열의 0번째 - 페이지 번호 숫자, 1번째 - 페이지 URL
     * @return
     */
    /**
     * String 배열
     * 0번째 - Page 번호 숫자
     * 1번째 - Page URL
     *
     * 현재 구간의 Page 가져와 구간별 Page 이동 버튼
     *
     * @return
     */


    public List<String[]> getPages() {
        if (total == 0) {
            return Collections.EMPTY_LIST;
        }

        List<String[]> pages = new ArrayList<>();
        for (int i = firstRangePage; i <= lastRangePage; i++) {
            String url =  baseUrl + i;
            pages.add(new String[] {"" + i, url}); // url : 쿼리스트링이 포함된 url임
        }

        return pages;
    }
}

