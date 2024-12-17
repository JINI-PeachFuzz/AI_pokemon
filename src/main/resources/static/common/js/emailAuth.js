var commonLib = commonLib ?? {};


// 이메일 인증 코드 관련
commonLib.emailAuth = {
    timer : {
        seconds: 180, // 3분
        intervalId: null,
        // 타이머 초기화
        reset() {
            this.stop();
            this.seconds = 180;

            if (typeof callback === 'function') {
                callback(this.seconds); // 열린기능으로~
            }
        },
        // 타이머 중지
        stop() {
            if (this.intervalId) {
                clearInterval(this.intervalId);
            }

            if (typeof callback === 'function') {
                callback(this.seconds);
            }
        },
        // 타이머 시작
        start(callback) {
            if (this.seconds < 1) return;
            this.stop(); // 이중으로 될까봐 우선 중지부터

            this.intervalId = setInterval(function() {
                const seconds = --commonLib.emailAuth.timer.seconds;
                if (typeof callback === 'function') {
                    callback(seconds);
                }
            }, 1000);
        },
    },
    // 인증 코드 전송
    sendCode(email, timerCallback, successCallback) { // 센드코드하면 이때부터 시간이 흘러감
        const { ajaxLoad } = commonLib;
        const { timer } = this;
        (async() => {
            try {
                await ajaxLoad(`/api/email/auth/${email}`);
                timer.reset(timerCallback);
                timer.start(timerCallback); // 콜백함수를 한 이유??

                if (typeof successCallback === 'function') {
                    successCallback();
                }
            } catch (err) { //인증코드 발급 실패
                alert(err.message);
            }
        })();

    },
    // 인증 코드 검증
    verify(authCode, successCallback, failureCallback) {
        const { ajaxLoad } = commonLib;
        const { timer } = this;
        (async() => {
            try {
                await ajaxLoad(`/api/email/verify?authCode=${authCode}`);
                timer.stop(successCallback);
            } catch(err) {
                if (typeof failureCallback === 'function') {
                    failureCallback(err);
                }
            }
        })();
    },
};

// ajax 실패시 처리
function callbackAjaxFailure(err) {

}