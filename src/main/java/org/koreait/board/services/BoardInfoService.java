package org.koreait.board.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.board.controllers.BoardSearch;
import org.koreait.board.controllers.RequestBoard;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.BoardData;
import org.koreait.board.entities.QBoardData;
import org.koreait.board.exceptions.BoardDataNotFoundException;
import org.koreait.board.repositories.BoardDataRepository;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardInfoService {

    private final BoardConfigInfoService configInfoService;
    private final BoardDataRepository boardDataRepository;
    private final FileInfoService fileInfoService;
    private final JPAQueryFactory queryFactory;
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;
    private final ModelMapper modelMapper;
    private final Utils utils; // 모바일여부 확인위함 -> 설정달리할려고


    /**
     * 게시글 한개 조회
     *
     * @param seq
     * @return
     */
    public BoardData get(Long seq) {

        BoardData item = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        addInfo(item, true); // 추가 정보 처리

        return item;
    }

    public RequestBoard getForm(Long seq) {
        return getForm(get(seq));
    }

    /***
     * 수정 처리시 커맨드 객체 RequestBoard로 변환
     * @param item
     * @return
     */
    public RequestBoard getForm(BoardData item) {
        RequestBoard form = modelMapper.map(item, RequestBoard.class); // 이건 수정할때밖에 안쓰임
        form.setMode("edit");
        form.setBid(item.getBoard().getBid()); // 게시글 수정시 오류발생 -> 값이 안들어가는거 확인하고 추가했음

        return form;
    }

    /***
     * 게시글 목록
     * @param search
     * @return
     */
    public ListData<BoardData> getList(BoardSearch search) {
        // 조회를 위한 커맨드객체가 들어갈 예정
        int page = Math.max(search.getPage(), 1); // 기본적으로 1페이지가 나올수 있게
        Board board = null;
        int rowsPerPage = 0;
        // 시작번호 오프셋도 만들어줘야함
        List<String> bids = search.getBid(); // 이건 필수는 아님 / 그래서 값이 안들어올 수 있음
        if (bids != null && bids.isEmpty()) {
            board = configInfoService.get(bids.get(0));
            rowsPerPage = board.getRowsPerPage();
        }

        int limit = search.getLimit() > 0 ? search.getLimit() : rowsPerPage; // 예를 들면 10개 보기 / 기본설정대로 보여지지만 리미트가 몇인지에 따라서 그만큼보여주게 하는거
        int offset = (page - 1 ) * limit; // 쿼리dsl을 사용하므로 이렇게 해줬음 / 검색 시작위치용

        /* 검색 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder();
        QBoardData boardData = QBoardData.boardData;

        // 게시판 아이디 / 검색조건 추가
        if (bids != null && !bids.isEmpty()) {
            andBuilder.and(boardData.board.bid.in(bids));
        }

        // 분류 검색
        List<String> categories = search.getCategory();
        if (categories != null && !categories.isEmpty()) {
            andBuilder.and(boardData.category.in(categories));
        }

        /***
         * 키워드 검색
         * sopt
         *  - ALL - 제목 + 내용 + 작성자(작성자 + 이메일 + 회원명) // sopt값이 없을땐 ALL로 고정할거임
         *  - SUBJECT - 제목만
         *  - CONTENT - 내용만
         *  - SUBJECT_CONTENT - 제목 + 내용
         *  - POSTER - 작성자 + 이메일 + 회원명
         */
        String sopt = search.getSopt();
        String skey = search.getSkey();
        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            StringExpression subject = boardData.subject;
            StringExpression content = boardData.content;
            StringExpression poster = boardData.poster.concat(boardData.member.name).concat(boardData.member.email);

            StringExpression condition = null;
            if (sopt.equals("SUBJECT")) { // 제목 검색
                condition = subject;
            } else if (sopt.equals("CONTENT")) { // 내용 검색
                condition = content;
            } else if (sopt.equals("SUBJECT_CONTENT")) { // 제목 + 내용
                condition = subject.concat(content);
            } else if (sopt.equals("POSTER")) {
                condition = poster;
            } else { // 통합 검색
                condition = subject.concat(content).concat(poster);
            }

            andBuilder.and(condition.contains(skey));

        }

        // 회원 이메일
        List<String> emails = search.getEmail();
        if (emails != null && !emails.isEmpty()) {
            andBuilder.and(boardData.member.email.in(emails)); // 특정회원의 이메일로 조회해서 모아볼려고 추가
        }


        /* 검색 처리 E */

        JPAQuery<BoardData> query = queryFactory.selectFrom(boardData)
                .leftJoin(boardData.board) // 지연로딩
                .fetchJoin() // 바로가져오게 하는것도 추가
                .leftJoin(boardData.member)
                .fetchJoin()
                .where(andBuilder)
                .offset(offset) // 쿼리dsl 그때 검색 시작위치용으로 필수
                .limit(limit);

//        .orderBy(boardData.notice.desc(), boardData.createdAt.desc()) // 1번이 가장 상단위치 / 우선 공지사항이 가장 상단에 표시되도록 했음
//                .fetch(); // 쿼리빌딩

        /* 정렬 조건 처리 S */
        String sort = search.getSort();
        if (StringUtils.hasText(sort)) {
            String[] _sort = sort.split("_");
            String field = _sort[0];
            String direction = _sort[1];
            if (field.equals("viewCount")) {
                query.orderBy(direction.equalsIgnoreCase("DESC") ? boardData.viewCount.desc() : boardData.viewCount.asc());
            } else if (field.equals("commentCount")) {
                query.orderBy(direction.equalsIgnoreCase("DESC") ? boardData.commentCount.desc() : boardData.commentCount.asc());
            } else {
                query.orderBy(boardData.notice.desc(),boardData.createdAt.desc());
            }

        } else { // 기본 정렬 조건 - notice DESC, createdAt DESC
            query.orderBy(boardData.notice.desc(),boardData.createdAt.desc());
        }

        /* 정렬 조건 처리 E */

        List<BoardData> items = query.fetch();

        long total = boardDataRepository.count(andBuilder);

        items.forEach(this::addInfo); // 추가 정보 처리 // 추가 정보 처리도 필요하니까 이것도 추가해줬음

        int ranges = utils.isMobile() ? 5 : 10; // 게시판 설정이 없을때
        if (board != null) { // 게시판별 설정이 있는 경우
            ranges = utils.isMobile() ? board.getPageRangesMobile() : board.getPageRanges(); // 게시판설정 가져옴 / 그래서 설정을 우선적으로 작업해야함
        }

        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request); // 게시판설정반영을 추가한거

        return new ListData<>(items, pagination);
    }

    public ListData<BoardData> getList(String bid, BoardSearch search) {
        search.setBid(List.of(bid));

        return getList(search);
    }

    /***
     * 게시판별 최신 게시글
     * @param bid
     * @param limit
     * @return
     */
    public List<BoardData> getLatest(String bid, int limit) {
        BoardSearch search = new BoardSearch();
        search.setLimit(limit); // 리밋은 게시글갯수
        search.setBid(List.of(bid));

        ListData<BoardData> data = getList(search);

        return data.getItems(); // 최신게시글도 가져올수 있는 편의기능도 완성!

    }

    public List<BoardData> getLatest(String bid) {
        return getLatest(bid, 5);
    }

    /***
     * 로그인한 회원이 작성한 게시글 목록
     * @param search
     * @return
     */
    public ListData<BoardData> getMyList(BoardSearch search) {
        if (!memberUtil.isLogin()) {
            return new ListData<>(List.of(), null); // 오류방지를 위해 추가했음
        }

        Member member = memberUtil.getMember();
        String email = member.getEmail();
        search.setEmail(List.of(email)); // 이렇게 하면 본인것만 나옴

        return getList(search);
    }

    /***
     * 추가 정보 처리
     * // 파일, 이전게시글과 다음게시글의 정보
     * @param item
     */
    private void addInfo(BoardData item, boolean isView) {
        // 게시판 파일 정보 S
        String gid = item.getGid();
        item.setEditorImages(fileInfoService.getList(gid, "editor"));
        item.setAttachFiles(fileInfoService.getList(gid, "attach"));
        // 게시판 파일 정보 E

        // 이전, 다음 게시글
        if (isView) { // 보기 페이지 데이터를 조회하는 경우만 이전, 다음 게시글을 조회
            QBoardData boardData = QBoardData.boardData;
            Long seq = item.getSeq();

            BoardData prev = queryFactory.selectFrom(boardData)
                    .where(boardData.seq.lt(seq))
                    .orderBy(boardData.seq.desc()) // 직전게시글이면 바로앞에 있는 걸 가져와여해서 내림차순
                    .fetchFirst();

            BoardData next = queryFactory.selectFrom(boardData)
                    .where(boardData.seq.gt(seq)) // gt는 큰것중에서 가장 작은걸 가져와야해서 작은순으로 정렬
                    .orderBy(boardData.seq.asc())
                    .fetchFirst();

            item.setPrev(prev);
            item.setNext(next);

        }
    }

    private void addInfo(BoardData item) {
        addInfo(item, false);
    }

    /***
     * 게시글 번호와 게시판 아이디로 현재 페이지 구하기
     * @param seq
     * @param limit
     * @return
     */
    public int getPage(String bid, Long seq, int limit) {
        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(boardData.board.bid.eq(bid))
                .and(boardData.seq.goe(seq)); // 크거나 같다 goe

        long total = boardDataRepository.count(builder);
        int page = (int)Math.ceil((double)total / limit);

        return page;
    }
}
