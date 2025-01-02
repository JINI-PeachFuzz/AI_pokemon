var commonLib = commonLib ?? {};
commonLib.fileManager = {
    /**
    * 파일 업로드 처리
    *
    */
    upload(files, gid, location, single, imageOnly, done) {
        try {
            /* 유효성검사 S */
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

           if (!gid || !('' + gid).trim()) {
               throw new Error("잘못된 접근입니다.");
           }
           /* 유효성 검사 E */

            /* 전송 양식 만들기 S */
            const formData = new FormData();
            formData.append("gid", gid);
            formData.append("single", single); // 단일파일로 올라갈 수 있게 한정하겠다
            formData.append("imageOnly", imageOnly);
            formData.append("done", done);

            if (location) {
                formData.append("location", location);
            }

           for (const file of files) {
               formData.append("file", file);
           }

            /* 전송 양식 만들기 E */


            /* 양식 전송 처리 S */
            const {ajaxLoad} = commonLib;
            ajaxLoad ("/api/file/upload", function(items) {
                if (typeof callbackFileUpload === 'function') {
                    callbackFileUpload(items); // 파일업로드후에 뭐가 나올지 모르니 후속처리는 콜백으로 열린기능으로 만들어줌
                }
            }, 'POST', formData);

            window.fileEl = null;

            /* 양식 전송 처리 E */

        } catch (err) {
            alert(err.message);
            console.error(err);
        }
    },
    /**
    파일 등록번호로 파일 삭제

    @param seq : 파일 등록번호
    @param callback : 삭제 후 후속 처리 콜백 함수
    */
    delete(seq, callback) {
        const { ajaxLoad } = commonLib;
        ajaxLoad(`/api/file/delete/${seq}`, file => callback(file), 'DELETE');
    }
};

window.addEventListener("DOMContentLoaded", function() {
    const fileUploads = document.getElementsByClassName("file-upload");
    let fileEl = null;


    for (const el of fileUploads) {
       el.addEventListener("click", function() {
           const {gid, location, single, imageOnly, done} = this.dataset;

           if (!fileEl) {
               fileEl = document.createElement("input");
               fileEl.type = 'file';
           } else {
                fileEl.value = ''; // 초기화 // 없을땐 괜찮은데 있을 땐 초기화!
           }

            fileEl.gid = gid;
            fileEl.location = location;
            fileEl.imageOnly = imageOnly === 'true';
            fileEl.single = single === 'true';
            fileEl.multiple = !fileEl.single;  // false - 단일 파일 선택, true - 여러파일 선택 가능
            fileEl.done = done === 'true'; // 업로드 완료 하자마자 완료 처리

            fileEl.click();


            // 파일 선택시 - change 이벤트 발생
             fileEl.removeEventListener("change", fileEventHandler);
             fileEl.addEventListener("change", fileEventHandler);


       });

    }

    // 이미지를 추가로 넣을 때마다 새로 또 추가가 되는현상이 위 코드 안에있어서 그런게 아닐까 싶어서 밖으로 뺐음 // 이벤트핸들러가 새로 정의되기때문에 중복으로 나오는 문제 해결됨
    // for문안에 있으니까 이미지 첨부할때 파일이 2개씩 추가되는 현상있어서 포문밖으로 뺐음
    function fileEventHandler(e) {
        const files = e.currentTarget.files;
        const {gid, location, single, imageOnly, done} = fileEl;

        const { fileManager } = commonLib;
        fileManager.upload(files, gid, location, single, imageOnly, done);
    }

    // 드래그 앤 드롭 파일 업로드 처리
    const dragUploads = document.getElementsByClassName("drag-upload");
    for (const el of dragUploads) {
        el.addEventListener("dragover", function(e) {
            // 기본 동작 차단
            e.preventDefault();

        });

        el.addEventListener("drop", function(e) {
            //기본 동작 차단
            e.preventDefault();

            const files = e.dataTransfer.files;

            let {gid, location, single, imageOnly, done} = this.dataset;
            single = single === "true";
            imageOnly = imageOnly === "true";
            done = done === "true";

            if (single && files.length > 1) { // 단일 파일 업로드 이지만 여러개를 선택한 경우
                alert("하나의 파일만 업로드 하세요.");
                return;
            }

            const {fileManager} = commonLib;
            fileManager.upload(files,gid,location,single,imageOnly,done);
        });

    }

});