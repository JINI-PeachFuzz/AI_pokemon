window.addEventListener("DOMContentLoaded", function() {
    // 공통 양식 버튼 처리 S
    const tableActions = document.getElementsByClassName("table-action");
    for (const tableActions of tableActions) {
        const { formName } = tableActions.dataset;
        const frm = document[formName];
        if (!frm) continue; // 선택이 안되는 경우에 건너띄기

        const buttons = frm.querySelectorAll("button");

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