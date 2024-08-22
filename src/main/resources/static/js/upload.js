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



// function handleFormSubmit(e) {
//     e.preventDefault();
//     const form = document.getElementById('editForm-proof');
//     const formData = new FormData(form);
//
//     // 허용된 파일 확장자 배열
//     const allowedExtensions = ['pptx', 'docx', 'bmp', 'doc', 'gif', 'hwp', 'jpeg', 'pdf', 'png', 'txt', 'zip'];
//
//     const inputFiles = document.getElementById('chooseFile').files;
//     let validFiles = [];
//
//     // FormData에 파일들을 추가하기 전에 확장자 검사
//     Array.from(inputFiles).forEach((file, index) => {
//         const fileExtension = file.name.split('.').pop().toLowerCase();
//
//         if (allowedExtensions.includes(fileExtension)) {
//             formData.append('file' + index, file); // 파일을 개별적으로 추가
//             validFiles.push(file); // 유효한 파일을 리스트에 추가
//             console.log('Uploading file:', file.name);
//         } else {
//             console.warn('파일 업로드가 허용되지 않음:', file.name);
//         }
//     });
//
//     // 유효한 파일이 있을 때만 업로드 요청 전송
//     if (validFiles.length > 0) {
//         fetch('/upload', {
//             method: 'POST',
//             body: formData,
//         })
//             .then(response => response.json())
//             .then(data => {
//                 if (data.success) {
//                     displayFiles(document.getElementById("detailItemCode").value);  // 업로드된 파일 목록을 갱신
//                 } else {
//                     console.error("파일 업로드 실패:", data.message);
//                 }
//             })
//             .catch(error => {
//                 console.error("파일 업로드 중 오류 발생:", error);
//             });
//     } else {
//         console.warn('유효한 파일이 없습니다. 업로드 요청을 보내지 않습니다.');
//     }
// }




// function handleFormSubmit(e) {
//     e.preventDefault();
//     const form = document.getElementById('editForm-proof');
//     const formData = new FormData(form);
//
//     const inputFiles = document.getElementById('chooseFile').files;
//
//     // FormData에 파일들을 추가
//     inputFiles.forEach((file, index) => {
//         formData.append('file' + index, file); // 파일을 개별적으로 추가
//         console.log('Uploading file:', file.name);
//     });
//
//     // 서버로 폼 데이터 전송
//     // 확장자 검사 추가
//     fetch('/upload', {
//         method: 'POST',
//         body: formData,
//     })
//         .then(response => response.json())
//         .then(data => {
//             if (data.success) {
//                 displayFiles(document.getElementById("detailItemCode").value);  // 업로드된 파일 목록을 갱신
//             } else {
//                 console.error("파일 업로드 실패:", data.message);
//             }
//         })
//         .catch(error => {
//             console.error("파일 업로드 중 오류 발생:", error);
//         });
// }

// function handleFormSubmit(e) {
//     e.preventDefault();
//     const form = document.getElementById('editForm-proof');
//     const formData = new FormData(form);
//
//     // 드래그 앤 드롭된 파일들 추가
//     const droppedFiles = dropFile.getDroppedFiles();
//
//     // 드래그 앤 드롭된 파일이 제대로 넘어오는지 확인
//     console.log('Files to upload::', droppedFiles);  // 로그로 확인
//
//     const inputFiles = document.getElementById('chooseFile').files;
//
//     // 선택된 파일들과 드래그 앤 드롭된 파일들 합치기
//     const allFiles = [...droppedFiles, ...inputFiles];
//
//     // FormData에 파일들을 추가
//     allFiles.forEach((file, index) => {
//         formData.append('file' + index, file); // 파일을 개별적으로 추가
//         console.log('Uploading file:', file.name);
//     });
//
//     // 서버로 폼 데이터 전송
//     // 확장자 검사 추가
//     fetch('/upload', {
//         method: 'POST',
//         body: formData,
//     })
//         .then(response => response.json())
//         .then(data => {
//             if (data.success) {
//                 displayFiles(document.getElementById("detailItemCode").value);  // 업로드된 파일 목록을 갱신
//             } else {
//                 console.error("파일 업로드 실패:", data.message);
//             }
//         })
//         .catch(error => {
//             console.error("파일 업로드 중 오류 발생:", error);
//         });
// }


// function handleFormSubmit(e) {
//     e.preventDefault();
//
//     const form = document.getElementById('editForm-proof');
//     const formData = new FormData(form);
//
//     // 드래그 앤 드롭된 파일들을 가져오기
//     const droppedFiles = dropFile.getDroppedFiles();
//     const inputFiles = document.getElementById('chooseFile').files;
//
//     // 선택된 파일들과 드래그 앤 드롭된 파일들을 FormData에 추가
//     const allFiles = [...droppedFiles, ...inputFiles];
//     allFiles.forEach(file => {
//         formData.append('file', file);  // 'file'은 서버에서 받아줄 필드명으로 변경 필요
//     });
//
//     const uploadPromises = allFiles.map((file) => {
//         const fileRef = storageRef.child(file.name);
//         return new Promise((resolve, reject) => {
//             const uploadTask = fileRef.put(file);
//             uploadTask.on(
//                 'state_changed',
//                 snapshot => {
//                     const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
//                     console.log(`Upload is ${progress}% done`);
//                 },
//                 error => {
//                     console.error('Upload failed:', error);
//                     reject(error);
//                 },
//                 () => {
//                     uploadTask.snapshot.ref.getDownloadURL().then((downloadURL) => {
//                         formData.append('files[]', downloadURL);  // Firebase 다운로드 URL 추가
//                         resolve(downloadURL);
//                     });
//                 }
//             );
//         });
//     });
//
//     // 모든 파일이 Firebase에 업로드된 후 서버에 폼 데이터 전송
//     Promise.all(uploadPromises)
//         .then(() => {
//             return fetch('/upload', {
//                 method: 'POST',
//                 body: formData,
//             });
//         })
//         .then(response => response.json())
//         .then(data => {
//             if (data.success) {
//                 displayFiles(document.getElementById("detailItemCode").value);  // 업로드된 파일 목록 갱신
//             } else {
//                 console.error("파일 업로드 실패:", data.message);
//             }
//         })
//         .catch(error => {
//             console.error("파일 업로드 중 오류 발생:", error);
//         });
// }


// function handleFormSubmit(e) {
//     e.preventDefault();
//
//     const form = document.getElementById('editForm-proof');
//     const formData = new FormData(form);
//
//     // Combine files from both input and drag-and-drop
//     const droppedFiles = dropFile.getDroppedFiles();
//     const inputFiles = document.getElementById('chooseFile').files;
//     const allFiles = [...droppedFiles, ...inputFiles];
//
//     // Check if files are correctly combined
//     console.log('All files:', allFiles);
//
//     const uploadPromises = allFiles.map(file => {
//         const fileRef = storageRef.child(file.name);
//         return new Promise((resolve, reject) => {
//             const uploadTask = fileRef.put(file);
//             uploadTask.on('state_changed',
//                 snapshot => {
//                     const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
//                     console.log('Upload is ' + progress + '% done');
//                 },
//                 error => {
//                     console.error('Upload failed:', error);
//                     reject(error);
//                 },
//                 () => {
//                     uploadTask.snapshot.ref.getDownloadURL().then(downloadURL => {
//                         formData.append('files[]', downloadURL);
//                         resolve(downloadURL);
//                     });
//                 }
//             );
//         });
//     });
//
//     Promise.all(uploadPromises)
//         .then(uploadedFiles => {
//             console.log('All files uploaded:', uploadedFiles);
//             return fetch('/upload', {
//                 method: 'POST',
//                 body: formData,
//             });
//         })
//         .then(response => response.json())
//         .then(data => {
//             if (data.success) {
//                 displayFiles(document.getElementById("detailItemCode").value);  // Refresh the uploaded files list
//             } else {
//                 console.error("File upload failed:", data.message);
//             }
//         })
//         .catch(error => {
//             console.error("Error during file upload:", error);
//         });
// }

// function handleFormSubmit(e) {
//     e.preventDefault();
//     const form = document.getElementById('editForm-proof');
//     const formData = new FormData(form);
//
//     // 드래그 앤 드롭된 파일들 추가
//     const droppedFiles = dropFile.getDroppedFiles();
//     // const droppedFiles = document.getElementById('dropFile').files;
//     const inputFiles = document.getElementById('chooseFile').files;
//
//     // 선택된 파일들과 드래그 앤 드롭된 파일들 합치기
//     const allFiles = [...droppedFiles, ...inputFiles];
//
//     const uploadPromises = [];
//
//     allFiles.forEach((file) => {
//         const fileRef = storageRef.child(file.name);
//         const uploadTask = fileRef.put(file);
//
//         const uploadPromise = new Promise((resolve, reject) => {
//             uploadTask.on(
//                 'state_changed',
//                 function(snapshot) {
//                     const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
//                     console.log('Upload is ' + progress + '% done');
//                     switch (snapshot.state) {
//                         case firebase.storage.TaskState.PAUSED: // or 'paused'
//                             console.log('Upload is paused');
//                             break;
//                         case firebase.storage.TaskState.RUNNING: // or 'running'
//                             console.log('Upload is running');
//                             break;
//                     }
//                 },
//                 function(error) {
//                     console.error('Upload failed:', error);
//                     reject(error);
//                 },
//                 function() {
//                     uploadTask.snapshot.ref.getDownloadURL().then(function(downloadURL) {
//                         formData.append('files[]', downloadURL);
//                         resolve(downloadURL);
//                     });
//                 }
//             );
//         });
//
//         uploadPromises.push(uploadPromise);
//     });
//
//     // 모든 파일 업로드가 완료된 후 서버에 폼 데이터를 전송
//     Promise.all(uploadPromises)
//         .then((uploadedFiles) => {
//             fetch('/upload', {
//                 method: 'POST',
//                 body: formData,
//             })
//                 .then(response => response.json())
//                 .then(data => {
//                     if (data.success) {
//                         displayFiles(document.getElementById("detailItemCode").value);  // 업로드된 파일 목록을 갱신
//                     } else {
//                         console.error("파일 업로드 실패:", data.message);
//                     }
//                 })
//                 .catch(error => {
//                     console.error("파일 업로드 중 오류 발생:", error);
//                 });
//         })
//         .catch((error) => {
//             console.error('파일 업로드 중 오류 발생:', error);
//         });
// }
//

// document.getElementById('chooseFile').addEventListener('change', function(e) {
//     dropFile.handleFiles(e.target.files);
// });

// function handleFormSubmit() {
//     const form = document.getElementById('editForm-proof');
//     const formData = new FormData(form);
//
//     //firebase-storage에 파일 업로드
//     const uploadTask = storageRef.put(file);
//     uploadTask.on('state_changed', function(snapshot) {
//         const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
//         console.log('Upload is ' + progress + '% done');
//         switch (snapshot.state) {
//             case firebase.storage.TaskState.PAUSED: // or 'paused'
//                 console.log('Upload is paused');
//                 break;
//             case firebase.storage.TaskState.RUNNING: // or 'running'
//                 console.log('Upload is running');
//                 break;
//         }
//     })
//
//     // 드래그 앤 드롭된 파일들 추가
//     const droppedFiles = dropFile.getDroppedFiles();
//     for (let i = 0; i < droppedFiles.length; i++) {
//         formData.append('file', droppedFiles[i]);
//     }
//
//     // 선택된 파일들 추가
//     const inputFiles = document.getElementById('chooseFile').files;
//     for (let i = 0; i < inputFiles.length; i++) {
//         formData.append('file', inputFiles[i]);
//     }
//
//     // 폼 데이터를 서버로 전송
//     fetch('/upload', {
//         method: 'POST',
//         body: formData,
//     })
//         .then(response => response.json())
//         .then(data => {
//             if (data.success) {
//                 displayFiles(document.getElementById("detailItemCode").value);  // 업로드된 파일 목록을 갱신
//             } else {
//                 console.error("파일 업로드 실패:", data.message);
//             }
//         })
//         .catch(error => {
//             console.error("파일 업로드 중 오류 발생:", error);
//         });
// }
//
// document.getElementById("modal-button-proof").addEventListener("submit", handleFormSubmit);

// // 파일 업로드를 위한 AJAX 요청 추가
// async function uploadFile(file, detailItemCode) {
//
//     //firebase-storage에 파일 업로드
//     const uploadTask = storageRef.put(file);
//     uploadTask.on('state_changed', function(snapshot) {
//         const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
//         console.log('Upload is ' + progress + '% done');
//         switch (snapshot.state) {
//             case firebase.storage.TaskState.PAUSED: // or 'paused'
//                 console.log('Upload is paused');
//                 break;
//             case firebase.storage.TaskState.RUNNING: // or 'running'
//                 console.log('Upload is running');
//                 break;
//         }
//     })
//
//     let formData = new FormData();
//     formData.append("file", file);
//
//     let response = await fetch("/upload", {
//         method: "POST",
//         body: formData
//     });
//
//     if (response.ok) {
//         displayFiles(detailItemCode);  // 업로드된 파일 목록을 갱신
//     } else {
//         console.error("파일 업로드 실패");
//     }
// }
//
//
// //파일 업로드
// document.getElementById("modal-button-proof").addEventListener("submit", async function(e) {
//     e.preventDefault();
//
//     const detailItemCode = button.getAttribute("data-detail-item-code") || "";
//
//     document.getElementById("detailItemCode").value = detailItemCode;
//
//     //const detailItemCode = document.querySelector('input[name="detailItemCode"]').value;
//     const files = document.getElementById("chooseFile").files;
//
//     for (let i = 0; i < files.length; i++) {
//         await uploadFile(files[i], detailItemCode);
//     }
// });


// // 파일 업로드를 위한 AJAX 요청 추가
// async function uploadFile(file, detailItemCode) {
//
//     //firebase-storage에 파일 업로드
//     const uploadTask = storageRef.put(file);
//     uploadTask.on('state_changed', function(snapshot) {
//         const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
//         console.log('Upload is ' + progress + '% done');
//         switch (snapshot.state) {
//             case firebase.storage.TaskState.PAUSED: // or 'paused'
//                 console.log('Upload is paused');
//                 break;
//             case firebase.storage.TaskState.RUNNING: // or 'running'
//                 console.log('Upload is running');
//                 break;
//         }
//     })
//
//     let formData = new FormData();
//     formData.append("file", file);
//
//     let response = await fetch("/upload", {
//         method: "POST",
//         body: formData
//     });
//
//     if (response.ok) {
//         displayFiles(detailItemCode);  // 업로드된 파일 목록을 갱신
//     } else {
//         console.error("파일 업로드 실패");
//     }
// }
//
//
// //파일 업로드
// document.getElementById("modal-button-proof").addEventListener("submit", async function(e) {
//     e.preventDefault();
//
//     const detailItemCode = button.getAttribute("data-detail-item-code") || "";
//     document.getElementById("detailItemCode").value = detailItemCode;
//
//     //const detailItemCode = document.querySelector('input[name="detailItemCode"]').value;
//     const choosefiles = document.getElementById("chooseFile").files;
//     const droppedFiles = dropFile.getDroppedFiles(); // 드래그 앤 드롭된 파일들 가져오기
//
//     const files = [...choosefiles, ...droppedFiles]; // 선택된 파일과 드래그 앤 드롭된 파일 합치기
//
//     for (let i = 0; i < files.length; i++) {
//         await uploadFile(files[i], detailItemCode);
//     }
// });

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
                            <button class="download-btn" onclick="downloadFile('/download?fileName=${encodedFileName}&detailItemCode=${detailItemCode}&year=${year}', '${file.fileName}')">download</button>
<!--                            <a href="/download?fileName=${file.name}&detailItemCode=${detailItemCode}" download="${encodedFileName}">download</a>-->
<!--                            <a href="/download?fileName=${file.name}&detailItemCode=${detailItemCode}" download="${file.name}">download</a>-->
<!--                            <button class="download-btn" onclick="downloadFile('${file.url}')">download</button>-->
<!--                            <input type="button" class="download-btn" onclick="downloadFile('${file.url}', '${file.name}')">-->
<!--                            <a href="${file.url}" download="${file.name}">download</a>-->
<!--                            <a onclick="downloadFile('${file.url}', '${file.name}')">download</a>-->                            
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

