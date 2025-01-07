package org.koreait.board.services.configs;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.board.controllers.BoardConfigSearch;
import org.koreait.admin.board.controllers.RequestBoard;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.QBoard;
import org.koreait.board.exceptions.BoardNotFoundException;
import org.koreait.board.repositories.BoardRepository;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigInfoService {

    private final BoardRepository boardRepository;
    private final HttpServletRequest request;
    private final ModelMapper modelMapper;

    /**
     * 게시판 설정 하나 조회
     *
     * @param bid
     * @return
     */
    public Board get(String bid) {
        Board item = boardRepository.findById(bid).orElseThrow(BoardNotFoundException::new); // 옵셔널!

        addInfo(item); // 추가 정보 처리

        return item;
    }

    public RequestBoard getForm(String bid) {
        Board item = get(bid);

        RequestBoard form = modelMapper.map(item, RequestBoard.class);
        form.setMode("edit"); // 이거는 수정외엔 사용할 일이 없어서 모드값을 수정으로 해놨음

        return form;
    }

    /**
     * 게시판 설정 목록
     *
     * @param search
     * @return
     */
    public ListData<Board> getList(BoardConfigSearch search) {
        int page = Math.max(search.getPage(), 1); // 없을 땐 1로 고정
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;

        BooleanBuilder andBuilder = new BooleanBuilder(); // 검색도 필요하니까
        QBoard board = QBoard.board;

        /* 검색 처리 S */
        String sopt = search.getSopt();
        String skey = search.getSkey();
        sopt = StringUtils.hasText(sopt) ? sopt : "ALL"; // sopt없을땐 기본값 ALL
        if (StringUtils.hasText(skey)) { // hasText가 공백도 체크함
            StringExpression condition;
            if (sopt.equals("BID")) { // 게시판 아이디
                condition = board.bid;
            } else if (sopt.equals("NAME")) { // 게시판명
                condition = board.name;
            } else { // 통합 검색 - 게시판 아이디 + 게시판명
                condition = board.bid.concat(board.name);
            }

            andBuilder.and(condition.contains(skey.trim()));
        }

        List<String> bids = search.getBid(); // 검색조건 추가
        if (bids != null && !bids.isEmpty()) { // 널이아니고 비어있지않을때
            andBuilder.and(board.bid.in(bids)); // 포함되어있는지도 체크
        }
        /* 검색 처리 E */

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));
        // PageRequest는 0부터 시작해서 0 이 1페이지임(첫페이지) 그래서 -1을 입력한거
        Page<Board> data = boardRepository.findAll(andBuilder, pageable); // 전체갯수가 담겨있는 데이터

        List<Board> items = data.getContent(); // 조회된 데이터를 가져옴
        items.forEach(this::addInfo); // 2차가공

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit, request);
        // (int page, int total, int ranges, int limit, HttpServletRequest request)

        return new ListData<>(items, pagination);
    }

    // 추가 정보처리 / 보드-엔티티-보드 하단 추가했음
    private void addInfo(Board item) {
        String category = item.getCategory();
        if (StringUtils.hasText(category)) {
            List<String> categories = Arrays.stream(category.split("\\n"))
                    .map(s -> s.replaceAll("\\r", ""))
                    .filter(s -> !s.isBlank()) // 중간에 비어있는경우 제외하기 위해서 사용함
                    .map(String::trim)
                    .toList();

            item.setCategories(categories);
        }
    }
}