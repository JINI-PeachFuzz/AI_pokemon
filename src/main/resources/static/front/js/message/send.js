window.addEventListener("DOMContentLoaded", function() {
    const { loadEditor } = commonLib;

    loadEditor("content", 350)
        .then((editor) => {
            window.editor = editor; // 전역 변수로 등록, then 구간 외부에서도 접근 가능하게 처리
        });

    // 이미지 본문 추가 이벤트 처리
    const insertEditors = document.querySelectorAll(".insert-editor")
    insertEditors.forEach(el => {
        el.addEventListener("click", e => insertImage(e.currentTarget.dataset.url));
    });

    // 파일 삭제 버튼 이벤트 처리
    const removeEls = document.querySelectorAll(".file-item .remove");
    const { fileManager } = commonLib;
    removeEls.forEach(el => {
        el.addEventListener("click", e => {
            if (confirm('정말 삭제하겠습니까?')) {
                const seq = e.currentTarget.dataset.seq;
                fileManager.delete(seq, () => {
                    const el = document.getElementById(`file-${seq}`);
                    el.parentElement.removeChild(el);
                });
            }
        });
    });
});

/**
* 파일 업로드 완료 후 성공 후속처리
*/

function callbackFileUpload(files) { // 콜백함수를 통해 열린기능으로 사용가능
    if (!files || files.length === 0) {
        return;
    }

//    console.log(files); // 첨부파일관련하여 문제확인하기 위해 확인작업으로 적었던거

    const imageUrls = [];

    const targetEditor = document.getElementById("editor-files");
    const targetAttach = document.getElementById("attach-files");
    const tpl = document.getElementById("tpl-file-item").innerHTML;

    const domParser = new DOMParser();

    const { fileManager } = commonLib; // 이미지 삭제하기 위해서 추가한거

    for (const {seq, fileUrl, fileName, location} of files) {
        let html = tpl;
        html = html.replace(/\[seq\]/g, seq) // 전역에서 교체해줄거
                    .replace(/\[fileName\]/g, fileName)
                    .replace(/\[fileUrl\]/g, fileUrl);

        const dom = domParser.parseFromString(html, "text/html");
        const fileItem = dom.querySelector(".file-item");
        const el = fileItem.querySelector(".insert-editor"); // else안에 있던걸 여기로 옮겼음
        const removeEl = fileItem.querySelector(".remove"); // 파일삭제시

        if (location === 'editor') { // 에디터에 추가될 이미지
            imageUrls.push(fileUrl); // push 로 필요한걸 하나씩 추가하는 형태

            targetEditor.append(fileItem);
            el.addEventListener("click", function() {
                const { url } = this.dataset; // 얘는 하나만 들어오는거
                insertImage(url); // 이미지 첨부기능
            });

        } else { // 다운로드를 위한 첨부 파일

            el.parentElement.removeChild(el);

            targetAttach.append(fileItem);
        }

        removeEl.addEventListener("click", function() {
            if (!confirm('정말 삭제하시겠습니까?')) {
                return;
            }

            fileManager.delete(seq, f => {
                const el = document.getElementById(`file-${f.seq}`);
                if (el) el.parentElement.removeChild(el); // 부모쪽에서 바로 삭제하는 거기때문에 parentEl로...
            });
        });
    }


    if (imageUrls.length > 0) insertImage(imageUrls);// insertImage를 이용해서 추가한다고 보면됨.

}

function insertImage(imageUrls) {
    // 얘가 배열이아닌 문자열로 들어왔을 경우
    imageUrls = typeof imageUrls === 'string' ? [imageUrls] : imageUrls; // 예를들어 이미지를 하나만 추가했다가 또 2~3개 더 추가할 수 도 있는거고.. 그럴경우를 대비해서 배열로 만들어주는거라 보면됨.
    // 통일성있게 담아줌

    editor.execute('insertImage', { source: imageUrls }); // insertImage 이미 정해져있는 명령어! / location 값이랑 fileName, seq, file URL 이 필요함
}