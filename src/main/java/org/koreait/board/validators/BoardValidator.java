package org.koreait.board.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.board.controllers.RequestBoard;
import org.koreait.board.entities.QCommentData;
import org.koreait.board.repositories.BoardDataRepository;
import org.koreait.board.repositories.CommentDataRepository;
import org.koreait.global.exceptions.scripts.AlertBackException;
import org.koreait.global.libs.Utils;
import org.koreait.global.validators.PasswordValidator;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Lazy
@Component
@RequiredArgsConstructor
public class BoardValidator implements Validator, PasswordValidator {

    private final MemberUtil memberUtil;
    private final Utils utils;
    private final BoardDataRepository boardDataRepository;
    private final CommentDataRepository commentDataRepository;
    private final

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestBoard.class);
    } // 서포트 -> 검증하고자하는

    @Override
    public void validate(Object target, Errors errors) {

        RequestBoard form = (RequestBoard) target; // 게스트비번에 대한 체크
        // 비회원 비밀번호 검증
        if (!memberUtil.isLogin()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "guestPw", "NotBlank");

            // 대소문자 구분없이 알파벳 1자 이상, 숫자 1자 이상 포함
            String guestPw = form.getGuestPw(); // 값이 있을 때는 복잡성에 대한것도 해줘야함
            if (StringUtils.hasText(guestPw) && (!alphaCheck(guestPw, true) || !numberCheck(guestPw))) {
                errors.rejectValue("guestPw", "Complexity");
            }

            String confirmPw = form.getConfirmPw();
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPw", "NotBlank");

            if (StringUtils.hasText(guestPw) && StringUtils.hasText(confirmPw) && !guestPw.equals(confirmPw)) {
                errors.rejectValue("confirmPw", "Mismatch");
            }
        }

        // 수정일때 seq 필수 여부
        String mode = form.getMode();
        Long seq = form.getSeq();
        if (mode != null && mode.equals("edit") && (seq == null || seq < 1L)) {
            errors.rejectValue("seq", "NotNull");
        }
    }

    /***
     * 게시글 삭제 가능 여부 체크
     * - 댓글이 존재하면 삭제 불가
     * @param seq
     */
    public  void checkDelete(Long seq) {
        QCommentData commentData = QCommentData.commentData;

        if (commentDataRepository.count(commentData.data.seq.eq(seq)) > 0L) {
            throw new AlertBackException(utils)
        }

    }
}
