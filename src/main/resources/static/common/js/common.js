var commonLib = commonLib ?? {};

/*
메타 태그 정보 조회
    mode - rootUrl : <meta name="rootUrl" ... />
*/
commonLib.getMeta = function(mode){
    if (!mode) return;

    const el = document.querySelector(`meta[name='${mode}']`);

    return el?.content; // el값이 없으면 undifined가 나옴 / ?. : 옵셔널 체이닝문법
};

// 자바스크립트에서 만든 주소에 컨택스트 경로 추가
commonLib.url = function(url) {
    return `${commonLib.getMeta('rootUrl').replace("/", "")}${url}`;
};

/*
Ajax 요청 처리

@params url : 요청 주소, http[s] : 외부 URL - 컨텍스트 경로는 추가 X
@params method 요청방식 - GET, POST, DELETE, PATCH ...
@params callback 응답 완료 후 후속 처리 콜백 함수
@params data : 요청 데이터 ( 바디가 있을 때 가능함 / 'POST', 'PUT', 'PATCH' 일때 )
@params headers : 추가 요청 헤더
*/

commonLib.ajaxLoad = function(url, callback, method = 'GET', data, headers, isText = false) {
    if (!url) return; // url없으면 처리안할꺼임

    const { getMeta } = commonLib;
    const csrfHeader = getMeta("_csrf_header");
    const csrfToken = getMeta("_csrf");
    url = /^http[s]?:/.test(url) ? url : commonLib.url(url);

    headers = headers ?? {};
    headers[csrfHeader] = csrfToken;
    method = method.toUpperCase();

    const options = {
        method,
        headers,
    }

    if (data && ['POST', 'PUT', 'PATCH'].includes(method)) { // body 쪽 데이터 추가 가능
        options.body = data instanceof FormData ? data : JSON.stringify(data);
    }

// resolve는 성공시, reject는 실패시!
    return new Promise((resolve, reject) => {
        fetch(url, options)
            .then(res => {
                if (res.status !== 204)
                    return isText ? res.text() : res.json();
                else {
                    resolve();
                }
            }) // 바디데이터 없을 경우를 대비해서 코드 추가함 204가 아닐 경우 JSON파일로 받겠다!
            .then(json => {
                if (isText) {
                    resolve(json);
                    return;
                }


                if (json?.success) { // 응답 성공(처리 성공) // ?. 은 옵셔널체이닝
                   if (typeof callback === 'function') { // 콜백 함수가 정의된 경우
                        callback(json.data);
                   }
                   resolve(json);

                   return;
                }

                reject(json); // 처리 실패
            })
            .catch(err => {
                console.error(err);

                reject(err); // 응답 실패
            });
    }); // Promise
};

/**
* 레이어 팝업
*
*/
commonLib.popup = function(url, width = 350, height = 350, isAjax = false, message) {
// 팝업 마지막 매개변수에 message를 추가해서 있으면 띄워주는걸로

    /* 레이어팝업 요소 동적 추가 S */
    const layerEls = document.querySelectorAll(".layer-dim, .layer-popup");
    layerEls.forEach(el => el.parentElement.removeChild(el));

    const layerDim = document.createElement("div");
    layerDim.className = "layer-dim";

    const layerPopup = document.createElement("div");
    layerPopup.className = "layer-popup";

    /* 레이어 팝업 가운데 배치 S */
    const xpos = (innerWidth - width) / 2;
    const ypos = (innerHeight - height) / 2;
    layerPopup.style.left = xpos + "px";
    layerPopup.style.top = ypos + "px";
    layerPopup.style.width = width + "px";
    layerPopup.style.height = height + "px";
    /* 레이어 팝업 가운데 배치 E */

    /* 레이어 팝업 컨텐츠 영역 추가 */
    const content = document.createElement("div");
    content.className="layer-content";
    layerPopup.append(content);

    /* 레이어 팝업 닫기 버튼 추가 S */
    const button = document.createElement("button");
    const icon = document.createElement("i");
    button.className = "layer-close";
    button.type = "button";
    icon.className = "xi-close";
    button.append(icon);
    layerPopup.prepend(button);

    button.addEventListener("click", commonLib.popupClose);
    /* 레이어 팝업 닫기 버튼 추가 E */

    document.body.append(layerPopup);
    document.body.append(layerDim);


    /* 레이어팝업 요소 동적 추가 E */

    /* 팝업 컨텐츠 로드 S */
    if (isAjax) { // 컨텐트를 ajax로 로드
        const { ajaxLoad } = commonLib;
        ajaxLoad(url, null, 'GET', null, null, true)
            .then((text) => content.innerHTML = text);
    } else if (message) { // 메세지 팝업 // 콘텐츠에다가 아래코드를 넣어주면 띄워주게 됨.
        content.innerHTML = `<div class='message'>
                                        <i class='xi-info'></i>
                                        ${message}
                                     </div>`;
    } else { // iframe으로 로드
        const iframe = document.createElement("iframe");
        iframe.width = width - 80;
        iframe.height = height - 80;
        iframe.frameBorder = 0;
        iframe.src = commonLib.url(url);
        content.append(iframe);
    }
    /* 팝업 컨텐츠 로드 E */
}

/**
* 메세지 출력 팝업
*
*/
commonLib.message = function(message, width = 350, height = 200) {
    commonLib.popup(null, width, height, false, message); // false는 ajax가 아니라서 펄스임
    // commonLib.popup = function(url, width = 350, height = 350, isAjax = false, message) 순서 참고
};


/**
* 레이어팝업 제거
*
*/
commonLib.popupClose = function() {
    const layerEls = document.querySelectorAll(".layer-dim, .layer-popup");
    layerEls.forEach(el => el.parentElement.removeChild(el));
};



/**
* 위지윅 에디터 로드
*
*/
commonLib.loadEditor = function(id, height = 350) {

    if (typeof ClassicEditor === 'undefined' || !id) {
        return Promise.resolve(); // 프로미스 then반환하기 위해서. / 오류발생해서 추가했음
    }

    return new Promise((resolve, reject) => {
        (async() => {
            try {
                const editor = await ClassicEditor.create(document.getElementById(id)); // ClassicEditor는 상단툴바
                resolve(editor);
                editor.editing.view.change((writer) => {
                    writer.setStyle(
                           "height",
                           `${height}px`,
                           editor.editing.view.document.getRoot()
                        );
                });
//                editor.ui.view.editable.element.style.height = '${height}px';

                /*
                // class 속성 값으로 노드 가져오기 / 여러개 선택
                const editorAreas = document.getElementsByClassName("ck-editor__editable");
                for (const el of editorAreas) {
                    el.style.height = `${height}px`;
                } // id와 height만 지정해주면 범용기능으로 사용할 수 있게 해줌*/

            } catch (err) {
                console.error(err);

                reject(err);
            }
        })();
    });

};

commonLib.insertEditorImage = function(imageUrls, editor) {
    editor = editor ?? window.editor;
    if (!editor) return; // 두번째 매개변수는 필수는 아니지만 쓰고싶을때 쓸수 있게 공통기능으로 추가했음

    imageUrls = typeof imageUrls === 'string' ? [imageUrls] : imageUrls; // 예를들어 이미지를 하나만 추가했다가 또 2~3개 더 추가할 수 도 있는거고.. 그럴경우를 대비해서 배열로 만들어주는거라 보면됨.
    // 통일성있게 담아줌

    editor.execute('insertImage', { source: imageUrls }); // insertImage 이미 정해져있는 명령어! / location 값이랑 fileName, seq, file URL 이 필요함
};

commonLib.selectImage = function(seq)
for (const el of showPopups) {
        el.addEventListener("click", function() {
            const { url, width, height } = this.dataset;
            commonLib.popup(url, width, height);
        });
    }
    이부분 확인해보기


window.addEventListener("DOMContentLoaded", function() {
    // 체크박스 전체 토글 기능 S
    const checkAlls = document.getElementsByClassName("check-all");
    for (const el of checkAlls) {
        el.addEventListener("click", function() {
            const { targetClass } = this.dataset;
            if (!targetClass) { // 토클할 체크박스의 클래스가 설정되지 않은 경우는 진행 X
                return;
            }

            const chks = document.getElementsByClassName(targetClass);
            for (const chk of chks) {
                chk.checked = this.checked;
            }
        });
    }
    // 체크박스 전체 토글 기능 E



    // 팝업 버튼 클릭 처리 S
    const showPopups = document.getElementsByClassName("show-popup");
    for (const el of showPopups) {
        el.addEventListener("click", function() {
            const { url, width, height } = this.dataset;
            commonLib.popup(url, width, height);
        });
    }
    // 팝업 버튼 클릭 처리 E
});