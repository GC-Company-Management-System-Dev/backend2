// CSRF 토큰 설정
var token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
var header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// 증적자료 업로드 함수
function DropFile(dropAreaId, fileListId) {
    let dropArea = document.getElementById(dropAreaId);
    let fileList = document.getElementById(fileListId);
    let detailItemCode = document.getElementById("detailItemCode").value; // Detail Item Code from your context
    let creator = "creatorName"; // Set the creator name appropriately
    let droppedFiles = []; // 드래그 앤 드롭으로 추가된 파일들을 저장할 배열

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    function highlight(e) {
        preventDefaults(e);
        dropArea.classList.add("highlight");
    }

    function unhighlight(e) {
        preventDefaults(e);
        dropArea.classList.remove("highlight");
    }

    function handleDrop(e) {
        unhighlight(e);
        let dt = e.dataTransfer;
        let files = dt.files;
        handleFiles(files);

        if (fileList) {
            fileList.scrollTo({ top: fileList.scrollHeight });
        }
    }

    function handleFiles(files) {
        files = [...files];
        droppedFiles.push(...files); // 드래그 앤 드롭된 파일들을 배열에 추가
        console.log('Dropped files:', droppedFiles);  // 로그로 확인
        files.forEach(previewFile);
    }

    function previewFile(file) {
        console.log(file);
        fileList.appendChild(renderFile(file));
    }

    function formatFileSize(sizeInBytes) {
        const kilobyte = 1024;
        const megabyte = kilobyte * 1024;
        const gigabyte = megabyte * 1024;

        if (sizeInBytes < kilobyte) {
            return sizeInBytes + ' Bytes';
        } else if (sizeInBytes < megabyte) {
            return (sizeInBytes / kilobyte).toFixed(2) + ' KB';
        } else if (sizeInBytes < gigabyte) {
            return (sizeInBytes / megabyte).toFixed(2) + ' MB';
        } else {
            return (sizeInBytes / gigabyte).toFixed(2) + ' GB';
        }
    }

    function renderFile(file) {
        let fileDOM = document.createElement("div");

        fileDOM.className = "file";
        fileDOM.innerHTML = `
        <div class="details">
            <div class="header">
                <span class="name">${file.name}</span>
                <span class="size">${formatFileSize(file.size)}</span>
                <button class="delete-dom-btn" onclick="deleteFileFromDOM(this.parentNode.parentNode.parentNode)">X</button>
            </div>
        </div>
    `;

        // 파일 엘리먼트를 업로드된 파일 목록에 추가
        const uploadedFiles = document.getElementById("uploaded-files");
        uploadedFiles.appendChild(fileDOM);

        return fileDOM;
    }

    dropArea.addEventListener("dragenter", highlight, false);
    dropArea.addEventListener("dragover", highlight, false);
    dropArea.addEventListener("dragleave", unhighlight, false);
    dropArea.addEventListener("drop", handleDrop, false);

    return {
        handleFiles,
        getDroppedFiles: () => droppedFiles // 드래그 앤 드롭된 파일들을 반환하는 함수 추가
    };

}

const dropFile = new DropFile("drop-file", "files");


// 업로드 전 X 버튼 클릭 시 DOM에서 삭제
function deleteFileFromDOM(fileDOM) {
    fileDOM.remove();
}

document.querySelector('#files').addEventListener('click', function(e) {
    if (e.target.classList.contains('delete-dom-btn')) {
        const fileDOM = e.target.closest('.file');
        deleteFileFromDOM(fileDOM);  // 서버에 업로드되지 않은 파일을 DOM에서만 삭제
    }
});

const firebaseConfig = {
    apiKey: "AIzaSyBdLTcjtlDsPCTtJ2vxszVHlVWgUvrz9Xs",
    authDomain: "scms-1862c.firebaseapp.com",
    projectId: "scms-1862c",
    storageBucket: "scms-1862c.appspot.com",
    messagingSenderId: "291919661509",
    appId: "1:291919661509:web:7d48db6b3089fb908963ac",
    measurementId: "G-0LVLEB0LE8"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
// const app = initializeApp(firebaseConfig);
// const analytics = getAnalytics(app);


const auth = firebase.auth();
const user = firebase.auth().currentUser;
const provider = new firebase.auth.GoogleAuthProvider();

// 스토리지 참조 생성
const storage = getStorage();
const storageRef = ref(storage);

// const storage = firebase.storage();
// const storageRef = storage.ref();
// const storageRef = firebase.storage().ref(fileName);

function isFileExtensionAllowed(fileName) {
    // 허용된 확장자
    const allowedExtensions = ['pptx', 'docx', 'bmp', 'doc', 'gif', 'hwp', 'jpeg', 'pdf', 'png', 'txt', 'zip'];

    const fileExtension = fileName.split('.').pop().toLowerCase();
    return allowedExtensions.includes(fileExtension);
}

function handleFormSubmit(e) {
    e.preventDefault();

    const form = document.getElementById('editForm-proof');
    const formData = new FormData(form);

    const inputFiles = document.getElementById('chooseFile').files;

    for (let file of inputFiles) {
        if (!isFileExtensionAllowed(file.name)) {
            alert(`허용되지 않은 파일 형식입니다: ${file.name}`);
            return; // 허용되지 않은 파일이 있으면 업로드를 중단
        }
        formData.append('files', file); // 파일 추가
    }

    fetch('/upload', {
        method: 'POST',
        body: formData,
        headers: {
            [header]: token
        }
    })
        .then(response => {
            // 응답의 Content-Type 확인
            const contentType = response.headers.get('content-type');

            if (contentType && contentType.includes('application/json')) {
                displayFiles(document.getElementById("detailItemCode").value);
                closeModal('editModal-proof');
                // JSON 응답인 경우
                return response.json();
            } else if (contentType && contentType.includes('text/html')) {
                displayFiles(document.getElementById("detailItemCode").value);
                closeModal('editModal-proof');
                // HTML 응답인 경우
                return response.text();  // HTML을 텍스트로 처리
            } else {
                throw new Error("알 수 없는 응답 형식입니다.");
            }
        })
        .then(data => {
            if (typeof data === 'object') {
                // JSON 응답 처리
                if (data.success) {
                    displayFiles(document.getElementById("detailItemCode").value);
                } else {
                    console.error("파일 업로드 실패:", data.message);
                }
            } else {
                // HTML 응답 처리
                console.log("서버로부터 HTML 응답을 받았습니다:", data);
                // 필요시 HTML 응답 처리 로직 추가
            }
        })
        .catch(error => {
            console.error("파일 업로드 중 오류 발생:", error);
        });
}

//파일 조회
function displayFiles(detailItemCode) {

    // detailItemCode에서 4번째와 5번째 글자를 추출하여 연도로 변환
    const year = "20" + detailItemCode.substring(3, 5); // "MNG2901006" -> "2029" 추출

    fetch(`/files/${detailItemCode}`)
        .then(response => response.json())
        .then(async files => {
            const uploadedFilesDiv = document.getElementById("uploaded-files");
            uploadedFilesDiv.innerHTML = ''; // 기존 내용을 초기화

            for (const file of files) {

                const fileElement = document.createElement("div");
                fileElement.className = "file";

                // 파일 이름을 URL 인코딩하여 전달
                const encodedFileName = encodeURIComponent(file.fileName);

                fileElement.innerHTML = `
                    <div class="details">
                        <div class="header">
                            <span class="name">${file.fileName}</span>
                            <span class="size">${formatFileSize(file.fileSize)}</span>
                            <button class="download-btn" style="border-radius: 5px" onclick="downloadFile('/download?fileName=${encodedFileName}&detailItemCode=${detailItemCode}&year=${year}', '${file.fileName}')">download</button>                            
                        </div>
                    </div>
                `;

                uploadedFilesDiv.appendChild(fileElement);
            }
        })
        .catch(error => {
            console.error("파일 목록 조회 중 오류 발생:", error);
        });
}

function formatFileSize(sizeInBytes) {
    const kilobyte = 1024;
    const megabyte = kilobyte * 1024;

    if (sizeInBytes < kilobyte) {
        return sizeInBytes + ' Bytes';
    } else if (sizeInBytes < megabyte) {
        return (sizeInBytes / kilobyte).toFixed(2) + ' KB';
    } else {
        return (sizeInBytes / megabyte).toFixed(2) + ' MB';
    }
}

// 파일 다운로드 함수
function downloadFile(url, fileName) {
    fetch(url)
        .then(response => response.blob())
        .then(blob => {
            const link = document.createElement("a");
            link.href = URL.createObjectURL(blob);
            link.download = fileName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        })
        .catch(error => console.error('다운로드 중 오류 발생:', error));
}

// 모달 창에서 파일 정보 조회
function displayFilesInModal(detailItemCode) {
    fetch(`/modal-files/${detailItemCode}`)
        .then(response => response.json())
        .then(files => {
            const uploadedFilesDiv = document.getElementById("uploaded-file");
            uploadedFilesDiv.innerHTML = ''; // 기존 내용을 초기화

            files.forEach(file => {
                const fileElement = document.createElement("div");
                fileElement.className = "file";

                fileElement.innerHTML = `
                        <div class="header">
                            <span class="name">${file.fileName}</span>
                            <span class="size">${formatFileSize(file.fileSize)}</span>
                            <button class="delete-btn" onclick="confirmDelete('${file.fileName}', '${detailItemCode}')">X</button>
                        </div>
                    `;

                uploadedFilesDiv.appendChild(fileElement);
            });
        })
        .catch(error => {
            console.error("파일 목록 조회 중 오류 발생:", error);
        });
}

// 파일 삭제 확인 및 처리
function confirmDelete(fileName, detailItemCode) {
    if (confirm(`${fileName} 파일을 정말 삭제하시겠습니까?`)) {
        deleteFile(fileName, detailItemCode);
    }
}

// 파일 삭제 요청
function deleteFile(fileName, detailItemCode) {
    console.log("Deleting file:", fileName, detailItemCode); // 로그 추가

    fetch(`/delete-file`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token, // CSRF 토큰을 헤더에 추가
        },
        body: JSON.stringify({ fileName, detailItemCode }),
    })
        .then(response => {
            if (response.ok) {
                console.log("File deletion successful"); // 성공 로그 추가
                displayFilesInModal(detailItemCode); // 파일 목록 갱신
            } else {
                console.error("File deletion failed:", response.status); // 실패 로그 추가
                alert("파일 삭제에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error("파일 삭제 중 오류 발생:", error);
        });
}

