package org.koreait.message.controllers.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.koreait.message.controllers.RequestMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Lazy
@Component
@RequiredArgsConstructor // 관리자인지 아닌지 체크해봐야하니까
public class MessageValidator implements Validator {

    private final MemberUtil memberUtil;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestMessage.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestMessage form = (RequestMessage) target;
        String email = form.getEmail();
        boolean notice = form.isNotice();
        if (!memberUtil.isAdmin() && notice) { // 관리자가 아닌데 공지쪽지를 보낼수 있으면 안됌
            notice = false;
            form.setNotice(notice);
        }

        if (!memberUtil.isLogin() && !notice && !StringUtils.hasText(email)) {
            errors.rejectValue("email", "NotBlank");

        }
    }
}
