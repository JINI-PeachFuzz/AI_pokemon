// commonLib 객체 없을 경우 새로 생성
var commonLib = commonLib ?? {};

commonLib.follow = {

    /**
     * Follow 기능
     *
     * @param seq
     * @param callbackFollow
     */
    async follow(seq, callbackFollow) {

        const {ajaxLoad} = commonLib;

        try {

            // 절대 경로 사용
            // /api/member 가 아닌 api/member 사용시 Controller 에서 Mapping 된 경로인 /mypage/about 이 url 앞에 붙어버림
            const res = await ajaxLoad(`/api/member/follow/${seq}`, null, "GET");

            if (typeof callbackFollow === 'function') {

                callbackFollow();

            }

        } catch (err) {

            alert(err.message);
        }
    },

    /**
     * Unfollow 기능
     *
     * @param seq
     * @param callbackUnfollow
     */
    async unfollow(seq, callbackUnfollow) {

        const {ajaxLoad} = commonLib;

        try {

            const res = await ajaxLoad(`/api/member/unfollow/${seq}`, null, "GET");

            if (typeof callbackUnfollow === 'function') {

                callbackUnfollow();

            }

        } catch (err) {

            alert(err.message);
        }
    }
};

window.addEventListener("DOMContentLoaded", function () {

    const followings = document.getElementsByClassName("follow_action");

    const { follow, unfollow } = commonLib.follow;

    // Follow & UnFollow 처리
    for (const el of followings) {

        el.addEventListener("click", async function() {

            const classList = this.classList;

            if (this.classList.contains("guest")) { // 미로그인 상태

                alert("로그인이 필요한 서비스 입니다.");

                // 비구조 할당
                const { pathname, search } = location;

                // search 는 항상 있는 것은 아니라 삼항조건으로 유무 체크 후 추가
                const redirectUrl = search ? pathname + search : pathname;

                // 로그인 후 원래 페이지로 돌아오도록
                location.href = commonLib.url(`/member/login?redirectUrl=${redirectUrl}`);

                return;
            }

            try {
                if (classList.contains("unfollow")) {

                    await commonLib.follow.unfollow(this.dataset.seq, function () {

                        classList.remove("unfollow");

                        el.innerText = "Follow";
                    });
                    location.reload();

                } else {

                    await commonLib.follow.follow(this.dataset.seq, function () {

                        classList.add("unfollow");

                        el.innerText = "Unfollow";

                    });
                    location.reload();
                }
            } catch (err) {

                alert(err.message);
            }
        });
    }
});