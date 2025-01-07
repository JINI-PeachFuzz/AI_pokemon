// 웹소켓관련
const webSocket = new WebSocket(`ws://${location.host}/msg`);

// 메시지컨트롤러쪽 데이터 가공 S 참고
// Map<String, Object> data = new HashMap<>();
// data.put("item", message);
// data.put("totalUnRead", totalUnRead); // JSON문자열로 바꿔서 보내볼거임
// 키와 밸류값

webSocket.addEventListener("message", function(data) { // 이게 연동될때마다 모든 유저에게 전송된다고 보면 될듯
    const email = commonLib.getMeta("user"); // user == 이메일

    const { item, totalUnRead } = JSON.parse(data.data); // 아이템이 공지사항인지 체크해봄
    let isShow = false; // 보일건지 말건지 결정
    if (item.notice || (email && email === item?.receiver?.email)) { // 공지사항
    // 중간에 ?를 넣으면 옵셔널 체이닝으로 없을 땐 널이 나옴
        isShow = true; // item.notice 아이템이 공지사항이면 트루가 되고 노출이 됨
    }


    if (isShow) { // 메세지 팝업
        commonLib.message("새로운 쪽지가 도착했습니다.");
    }
    console.log(totalUnRead, JSON.parse(data.data));

    if (totalUnRead > 0) {
        const badge = document.querySelector(".link-mypage .badge");
        if (badge) {
            badge.innerText = totalUnRead; // 뱃지가 있을때
            badge.classList.remove("dn"); // dn을 제거
        }
    }
});





