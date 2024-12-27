window.addEventListener("DOMContentLoaded", function() {
    // 공통 양식 버튼 처리 S
    const tableActions = document.getElementsByClassName("table-action");
    for (const tableAction of tableActions) {
        const { formName } = tableAction.dataset;
        const frm = document[formName];
        if (!frm) continue; // 선택이 안되는 경우에 건너띄기

        const buttons = tableAction.querySelectorAll("button");
        // 관리자페이지에서 정보수정눌렀을 때 정말 수정하겠습니까란 팝업창이 떠서 하위인
        // tableActions로 변경해줬음

        for (const button of buttons) {
            button.addEventListener("click", function(){
                let method = "PATCH";
                if (this.classList.contains("delete")) {
                    method = "DELETE";
                }

                if (confirm(`정말 ${method === 'DELETE' ? '삭제' : '수정'}하겠습니까?`)) {
                    frm._method.value = method;
                    frm.submit();

                }
            });
        }
    }
    // 공통 양식 버튼 처리 E
});