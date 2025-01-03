package org.koreait.message.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Transient;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.paging.ListData;
import org.koreait.message.controllers.MessageSearch;
import org.koreait.message.entities.Message;
import org.koreait.message.entities.QMessage;
import org.koreait.message.exceptions.MessageNotFoundException;
import org.koreait.message.repositories.MessageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class MessageInfoService {

    private final MessageRepository messageRepository;
    private final JPAQueryFactory queryFactory; // 수신받는쪽관련 / 하나는 괜찮은데 목록조회시 필요할것 같아서

    /***
     * 쪽지 하나 조회
     * @param seq
     * @return
     */
    public Message get(Long seq) {

        Message item = messageRepository.findById(seq).orElseThrow(MessageNotFoundException::new); // 옵셔널 형태로 후속처리

        addInfo(item); // 추가 정보 처리 / editorImages, attachFiles

        return null;
    }

    /***
     * 쪽지 목록 조회
     * @param search
     * @return
     */
    public ListData<Message> getList(MessageSearch search) {
    // 본인이 보내고 받은것만 볼 수 있게 한정해줘야함
        int page = Math.max(search.getPage(),1); // 1페이지 이상나올거
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;
        int offset = (page -1) * limit; // 쿼리dsl을 사용할 거라서 넣어준거

        BooleanBuilder andBuilder = new BooleanBuilder();
        QMessage message = QMessage.message;

        List<Message> items = queryFactory.selectFrom(message)
                .leftJoin(message.receiver)
                .fetchJoin() // 처음부터 조회하고 바로 가져오겠다는 뜻
                .where(andBuilder)
                .limit(limit)
                .offset(offset)
                .orderBy(message.createdAt.desc()) // 최신 순서대로 나올수있게
                .fetch();

        items.forEach(this::addInfo); // 추가 정보 처리

        long total = messageRepository.count(andBuilder);



        return null;
    }

    /***
     * 추가 정보 처리
     * @param item
     */
    private void addInfo(Message item) {

    }
}