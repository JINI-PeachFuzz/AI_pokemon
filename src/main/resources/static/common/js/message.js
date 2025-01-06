// 웹소켓관련
const webSocket = new WebSocket(`ws://${location.host}/msg`);

webSocket.addEventListener("message", function(data) {
    console.log("message:", data);
}); // 이게 연동될때마다 모든 유저에게 전송된다고 보면 될듯


