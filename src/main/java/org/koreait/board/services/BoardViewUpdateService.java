package org.koreait.board.services;

import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.*;
import org.koreait.board.repositories.BoardDataRepository;
import org.koreait.global.libs.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardViewUpdateService {
    private final BoardDataRepository boardDataRepository;
    private final BoardViewRepository boardViewRepository;
    private final Utils utils;

    public long process(Long seq) {
        BoardData item = boardDataRepository.findById(seq).orElse(null); // 얘는 예외발생은 안하고 없으면 없는대로 넘기는걸로 처리할 거
        if (item == null) return 0L;

        try {
            BoardView view = new BoardView();
            view.setSeq(seq);
            view.setHash(utils.getMemberHash());
            boardViewRepository.saveAndFlush(view);

        } catch (Exception e) {}

        // 조회수 업데이트
        QBoardView boardView = QBoardView.boardView;
        long total = boardViewRepository.count(boardView.seq.eq(seq));

        item.setViewCount(total);
        boardDataRepository.saveAndFlush(item); // 쿠키는 지우면 사라지니까 DB에 넣는게 좋음

        return total;
    }
}
