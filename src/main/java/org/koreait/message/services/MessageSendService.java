package org.koreait.message.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.services.FileDoneService;
import org.koreait.member.entities.Member;
import org.koreait.member.exceptions.MemberNotFoundException;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.message.constants.MessageStatus;
import org.koreait.message.controllers.RequestMessage;
import org.koreait.message.entities.Message;
import org.koreait.message.repositories.MessageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class MessageSendService {
    private final MemberUtil memberUtil;
    private final MemberRepository memberRepository;
    private final MessageRepository repository; // 엔티티-메세지.자바에서 보내는사람 받는사람 정의해논거 있어서 의존성 위에 더 추가했음
    private final FileDoneService fileDoneService; // 파일업로드 완료 처리


    public void process(RequestMessage form) {

        String email = form.getEmail();
        Member receiver = !form.isNotice() ? memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new) : null; // 이메일이 없는 사람도 있으니까 추가한거 / orElse를 예외로 던져버리고 null은 전체 공지일때임

        Message message = Message.builder()
                .gid(form.getGid())
                .notice(form.isNotice())
                .subject(form.getSubject())
                .content(form.getContent())
                .sender(memberUtil.getMember()) // sender 대신에 바로 가져옴
                .receiver(receiver)
                .status(MessageStatus.UNREAD)
                .build();

        repository.saveAndFlush(message);
        fileDoneService.process(form.getGid()); // 파일 업로드 완료 처리
    }

}
