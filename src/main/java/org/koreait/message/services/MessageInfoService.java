package org.koreait.message.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.message.controllers.MessageSearch;
import org.koreait.message.entities.Message;
import org.koreait.message.entities.QMessage;
import org.koreait.message.exceptions.MessageNotFoundException;
import org.koreait.message.repositories.MessageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class MessageInfoService {

    private final FileInfoService fileInfoService;
    private final MessageRepository messageRepository;
    private final JPAQueryFactory queryFactory; // 수신받는쪽관련 / 하나는 괜찮은데 목록조회시 필요할것 같아서
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;
    private final Utils utils;

    /***
     * 쪽지 하나 조회
     * @param seq
     * @return
     */
    public Message get(Long seq) {
        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder orBuilder = new BooleanBuilder();
        QMessage message = QMessage.message;
        builder.and(message.seq.eq(seq));


        if (!memberUtil.isAdmin()) {
            Member member = memberUtil.getMember();
            BooleanBuilder orBuilder2 = new BooleanBuilder();
            BooleanBuilder andBuilder = new BooleanBuilder();
            orBuilder2.or(andBuilder.and(message.notice.eq(true)).and(message.receiver.isNull()))
                    // 공지사항이지만 리시버가 널인때는 보여져야함
                    .or(message.receiver.eq(member));

            orBuilder.or(message.sender.eq(member))
                    .or(orBuilder2);

            builder.and(orBuilder);
        }

        Message item = messageRepository.findOne(builder).orElseThrow(MessageNotFoundException::new); // 옵셔널 형태로 후속처리

        addInfo(item); // 추가 정보 처리 / editorImages, attachFiles

        return item;
    }

    /***
     * 쪽지 목록 조회
     * @param search
     * @return
     */
    public ListData<Message> getList(MessageSearch search) {
        // 본인이 보내고 받은것만 볼 수 있게 한정해줘야함
        int page = Math.max(search.getPage(), 1); // 1페이지 이상나올거
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;
        int offset = (page - 1) * limit; // 쿼리dsl을 사용할 거라서 넣어준거


        // 검색 조건 처리 S
        BooleanBuilder andBuilder = new BooleanBuilder();
        QMessage message = QMessage.message;
        String mode = search.getMode();
        Member member = memberUtil.getMember(); // 사용자정보가져오기

        mode = StringUtils.hasText(mode) ? mode : "receive";

        // send - 보낸 쪽지 목록, receive - 받은 쪽지 목록
        if (mode.equals("send")) {
            andBuilder.and(message.sender.eq(member));
        } else {
            BooleanBuilder orBuilder = new BooleanBuilder(); // 공지사항일때와 아닌때를 구분하기 위해서 불리언으로 했음
            BooleanBuilder andBuilder1 = new BooleanBuilder();

            orBuilder.or(andBuilder1.and(message.notice.eq(true)).and(message.receiver.isNull())) // 공지쪽지
                    .or(message.receiver.eq(member)); // 공지사항일때는 널임 / 널인지 아닌지로 체크해볼수있음

            andBuilder.and(orBuilder);
        }

        andBuilder.and(mode.equals("send") ? message.sender.eq(member) : message.receiver.eq(member));
        andBuilder.and(mode.equals("send") ? message.deletedBySender.eq(false) : message.deletedByReceiver.eq(false)); // 쪽지 삭제를 할때 양쪽에서 지워지면 안되기 때문에 따로따로 삭제하는걸 넣었음

        // 보낸사람 조건 검색
        List<String> sender = search.getSender();
        if (mode.equals("receive") && sender != null && !sender.isEmpty()) {
            andBuilder.and(message.sender.email.in(sender)); // 보낸사람 이메일 목록가지고 조회할 수 있는 기능
        }

        // 키워드 검색 처리
        String sopt = search.getSopt();
        String skey = search.getSkey();
        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";
        if (StringUtils.hasText(skey)) {
            StringExpression condition = sopt.equals("SUBJECT") ? message.subject : message.subject.concat(message.content);

            andBuilder.and(condition.contains(skey.trim()));
        }


        // 검색 조건 처리 E
        // queryFactory 은 sql문을 쓰기위해서 사용하는 거고 조회하기 위한거
        List<Message> items = queryFactory.selectFrom(message)
                .leftJoin(message.receiver) // 수신인 조회
                .fetchJoin() // 처음부터 조회하고 바로 가져오겠다는 뜻 // 처음부터 조인한 상태로 조회하겠다.
                .where(andBuilder) // where은 spl 조건문 // andBuilder는 모두가 참이어야함
                .limit(limit) // 한페이지에 몇개보여줄지
                .offset(offset) // 시작페이지 1번,11번 시작하는 번호
                .orderBy(message.notice.desc(), message.createdAt.desc()) // 최신 순서대로 나올수있게
                // 앞은 1차정렬, 뒤는 2차정렬 -> 공지사항이 항상 상단에 보여질 수 있게 한거
                .fetch();

        items.forEach(this::addInfo); // 추가 정보 처리

        long total = messageRepository.count(andBuilder);

        Pagination pagination = new Pagination(page, (int) total, utils.isMobile() ? 5 : 10, limit, request); // 페이지네이션 기초 페이지생성

        // (int page, int total, int ranges, int limit, HttpServletRequest request)

        return new ListData<>(items, pagination);
    }

    /***
     * 추가 정보 처리
     * @param item
     */
    private void addInfo(Message item) {
        String gid = item.getGid();
        item.setEditorImages(fileInfoService.getList(gid, "editor"));
        item.setAttachFiles(fileInfoService.getList(gid, "attach"));

        Member member = memberUtil.getMember();
        item.setReceived(
                (item.isNotice() && item.getReceiver() == null) || // 공지글을 받았는지 아닌지 체크
                        item.getReceiver().getSeq().equals(member.getSeq())
        ); // 수신한 쪽지인지 구분

        // 삭제 가능 여부
        boolean deletable = (item.isNotice() && memberUtil.isAdmin())
                || (!item.isNotice() && (item.getSender().getSeq().equals(member.getSeq())
                || item.getReceiver().getSeq().equals(member.getSeq())));
        item.setDeletable(deletable);
    }
}