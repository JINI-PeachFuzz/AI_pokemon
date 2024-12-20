window.addEventListener("DOMContentLoaded", function() {

    const sendButton = document.getElementById("send-auth-code");
    const authCodeEl = document.getElementById("auth-code");
    const verifyButton = document.getElementById("verify-auth-code");

    const { emailAuth } = commonLib;

    frmJoin.email.addEventListener("change", function() {
        if (this.value.trim()) {
            sendButton.disabled = false;
        } else { // 입력을 했을 때만 버튼 활성화
            sendButton.disabled = true;
        }
    });

    // 인증코드 전송 처리 S
    sendButton.addEventListener("click", function() {
        const email = frmJoin.email.value.trim();
        if (!email) {
            return;
        }

        /**
        코드 전송 완료 후 후속 처리
        1. 전송버튼의 문구를 인증코드 재전송으로 변경
        2. 타이머에 남은 시간을 출력
        */

        emailAuth.sendCode(email, updateTimer, function() {
            // 1. 전송버튼의 문구를 인증코드 재전송으로 변경,
            //    이메일을 변경하지 못하게 처리
            //    인증코드 입력 가능하게 처리
            //    인증하기 버튼 노출
            const { text } = sendButton.dataset;
            sendButton.innerText = text;

            frmJoin.email.setAttribute("readonly", true); // 바꾸지못하게 막은거 / readonly는 풀어버리고 바꿀 수 있게 할 수 있는 취약점도 있음
            authCodeEl.disabled = false; // join.html에서 처음에 disabled로 막아놨으니 이제 풀어준거

            verifyButton.classList.remove("dn");
        });
    });

    /**
    타이머 출력 갱신
    */

    function updateTimer(seconds) {
        let timerStr = "";
        if (seconds > 0) {
            const min = Math.floor(seconds / 60); // 분에 대한 것
            const sec = seconds - min * 60; // 남아있는 초
            timeStr = `${('' + min).padStart(2, '0')}:${('' + sec).padStart(2, '0')}`; // 0 채워넣기
        } else { // 타이머가 0이 되면 다시 이메일 변경 가능하게 처리, 인증 코드 입력 불가 처리, 인증하기 버튼 감추기
            timerStr = "00:00"; // 1초에서 멈추는 현상으로 인해 위에는 빈문자열을 넣고 여기에 00:00 을 추가해줬음
            frmJoin.email.removeAttribute("readonly");
            // 인증코드 입력하고 나면 다시 입력하지 못하게 막아야함
            authCodeEl.value = ""; // 막기전에 비워준거
            authCodeEl.disabled = true;
            verifyButton.classList.remove("dn");
            verifyButton.classList.add("dn");
        }

        const timerEl = document.querySelector(".auth-box .timer");
        if (timerEl) {
            timerEl.innerHTML = timeStr;
        }
    }
    // 인증코드 전송 처리 S

    // 인증 코드 확인 처리 S
    verifyButton.addEventListener("click", function() {
        const authCode = authCodeEl.value;
        if (!authCode || ('' + authCode).length < 5) { // authCode를 문자열로 할려고 결합했음
            alert("인증코드를 입력하세요.");
            authCodeEl.focus();
            return;
        }
        const el = document.querySelector(".auth-box .message");
        el.classList.remove("dn");

        emailAuth.verify(authCode, () => {
            // 인증 성공시
            // 1. "인증되었습니다." 메세지를 출력
            // 2. authCodeEl, verifyButton, sendButton, timer 제거
            el.innerText = "인증되었습니다.";
            const authBoxEl = document.querySelector(".auth-box").children[0];
            authBoxEl.parentElement.removeChild(authBoxEl); // 하나하나씩 제거하는 것보다 div통으로 제거하는게 좋을 거 같음

        }, (err) => {
            // 인증 실패시
            el.innerText = err.message;
        });


    });
    // 인증 코드 확인 처리 E
});

// 주소 검색 후 후속 처리 (회원 가입 양식)

function callbackAddressSearch(data) {
    if (!data) {
        return;
    }

    const { zipCode, address } = data;

    frmJoin.zipCode.value = zipCode;
    frmJoin.address.value = address;
}


