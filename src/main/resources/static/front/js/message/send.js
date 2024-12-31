window.addEventListener("DOMContentLoaded", function() {
    const { loadEditor } = commonLib;

    loadEditor("content", 450)
        .then((editor) => {
            window.editor = editor; // 전역 변수로 등록, then 구간 외부에서도 접근 가능하게 처리
        });
});

/**
* 파일 업로드 완료 후 성공 후속처리
*/

function callbackFileUpload(files) { // 콜백함수를 통해 열린기능으로 사용가능
    if (!files || files.length === 0) {
        return;
    }

    const imageUrls = [];

    const targetEditor = document.getElementById("editor-files");
    const targetAttach = document.getElementById("attach-files");
    const tpl = document.getElementById("tpl-file-item").innerHTML;

    const domParser = new DOMParser();

    for (const {seq, fileUrl, fileName, location} of files) {
        let html = tpl;
        html = html.replace(/\[seq\]/g, seq) // 전역에서 교체해줄거
                    .replace(/\[fileName\]/g, fileName)
                    .replace(/\[fileUrl\]/g, fileUrl);

        const dom = domParser.parseFromString(html, "text/html");
        const fileItem = dom.querySelector(".file-item");



        if (location === 'editor') { // 에디터에 추가될 이미지
            imageUrls.push(fileUrl); // push 로 필요한걸 하나씩 추가하는 형태

            targetEditor.append(fileItem)

        } else { // 다운로드를 위한 첨부 파일
            const el = fileItem.querySelector(".insert-editor");
            el.parentElement.removeChild(el);

            targetAttach.append(fileItem);
        }
    }


    if (imageUrls.length > 0) insertImage(imageUrls); // insertImage를 이용해서 추가한다고 보면됨.

}

function insertImage(imageUrls) {
    editor.execute('insertImage', { source: imageUrls }); // insertImage 이미 정해져있는 명령어! / location 값이랑 fileName, seq, file URL 이 필요함
}