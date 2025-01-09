package org.koreait.board.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.board.controllers.RequestBoard;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.BoardData;
import org.koreait.board.exceptions.BoardDataNotFoundException;
import org.koreait.board.repositories.BoardDataRepository;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.file.services.FileDoneService;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Lazy
@Service
@Transactional // 영속성유지 // set으로 가져온 엔티티 영속성 유지위해 추가함
@RequiredArgsConstructor
public class BoardUpdateService {

    private final BoardConfigInfoService configInfoService;
    private final BoardDataRepository boardDataRepository;
    private final MemberUtil memberUtil;
    private final PasswordEncoder passwordEncoder; // 해시화위함
    private final HttpServletRequest request; // 얘를 가지고 ip가져오고
    private final FileDoneService fileDoneService; // 유지처리 / 파일은 이게 done값인것만 조회가능하기때문에 완료처리 필요함

    public BoardData process(RequestBoard form) {

        Long seq = Objects.requireNonNullElse(form.getSeq(), 0L); // 있으면 수정 없으면 등록
        String mode = Objects.requireNonNullElse(form.getMode(), "write"); // 혹시나 없을 때나 널일수도 있으니까

        BoardData data = null;
        if (mode.equals("edit")) { // 수정시
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);
        } else { // 추가
            /***
             * 등록될때만 최초 한번 기록되는 데이터
             * - 게시판 설정, 회원
             * - gid
             * - ip, UserAgent // 같은건 변경되면 안되고 이걸로 통제하고 해야함
             */
            Board board = configInfoService.get(form.getBid()); // 내부에서 게시판이 없으면 던짐 / 그래서 여기서 검증이 한번더 됨
            data = new BoardData();
            data.setBoard(board);
            data.setMember(memberUtil.getMember());
            data.setGid(form.getGid());
            data.setIpAddr(request.getRemoteAddr()); // 해당사용자를 차단하거나 통제하거나 안드로이드거나 아이폰이거나 그런거...
            data.setUserAgent(request.getHeader("User-Agent"));
            // 이정보들은 수정시 변경되면 안되는 데이터기때문에 이렇게 가져왔음 // 최초등록데이터들만 set 함
        } // requestboard 커맨트객체 참고

        // 글등록, 글 수정시 공통 반영 사항
        String guestPw = form.getGuestPw();
        if (StringUtils.hasText(guestPw)) { // 비회원 비밀번호
            data.setGuestPw(passwordEncoder.encode(guestPw));
        }

        data.setPoster(form.getPoster());

        // notice(공지글) 여부는 관리자만 반영 가능
        if (memberUtil.isAdmin()) {
            data.setNotice(form.isNotice());
        }

        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setExternalLink(form.getExternalLink());
        data.setYoutubeUrl(form.getYoutubeUrl());
        data.setCategory(form.getCategory());

        boardDataRepository.saveAndFlush(data); // DB반영
        fileDoneService.process(form.getGid()); // 완료처리


        return data;
    }
}
