var commonLib = commonLib ?? {};
commonLib.fileManager = {
    /**
    * 파일 업로드 처리
    *
    */
    upload(files, gid, location, single, imageOnly) {
        try {
            if (!files || files.length === 0) { // 여기서 0은 갯수임 파일갯수 파일이 없을때를 얘기함
                throw new Error("파일을 선택하세요.");
            }

            if (imageOnly) { // 이미지만 업로드 하는 경우
                for (const file of files) {
                    if (file.type.indexOf("image/") === -1) { // 이미지가 아닌 파일인 경우 // 인덱스가 0부터 시작인데 그것보다도 없는걸 할려면 -1 이어야함
                        throw new Error("이미지 형식이 아닙니다.");
                    }
                }
            }

        } catch (err) {
            alert(err.message);
            console.error(err);
        }
    }

};

window.addEventListener("DOMContentLoaded", function() {
    const fileUploads = document.getElementsByClassName("file-upload");
    const fileEl = document.createElement("input");
    fileEl.type = 'file';

    for (const el of fileUploads) {
        el.addEventListener("click", function() {
            const {gid, location, single, imageOnly} = this.dataset;

            fileEl.gid = gid;
            fileEl.location = location;
            fileEl.imageOnly = imageOnly === 'true';
            fileEl.single = single === 'true';
            fileEl.multiple = !fileEl.single;  // false - 단일 파일 선택, true - 여러파일 선택 가능

            fileEl.click();
        });
    }

    // 파일 선택시 - change 이벤트 발생
    fileEl.addEventListener("change", function(e) {
        const files = e.currentTarget.files;
        const {gid, location, single, imageOnly} = fileEl;

        const { fileManager } = commonLib;
        fileManager.upload(files, gid, location, single, imageOnly);
    });
});