// 증적자료 업로드 함수
function DropFile(dropAreaId, fileListId) {
    let dropArea = document.getElementById(dropAreaId);
    let fileList = document.getElementById(fileListId);
    let detailItemCode = document.getElementById("detailItemCode").value; // Detail Item Code from your context
    let creator = "creatorName"; // Set the creator name appropriately

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
<!--                <a href="/download?fileName=${file.name}&detailItemCode=${detailItemCode}" class="download-btn">Download</a> &lt;!&ndash; 다운로드 버튼 추가 &ndash;&gt;-->
            </div>
        </div>
    `;

        // 파일 엘리먼트를 업로드된 파일 목록에 추가
        const uploadedFiles = document.getElementById("uploaded-files");
        uploadedFiles.appendChild(fileDOM);

        return fileDOM;
    }


    // function getUploadedFiles() {
    //     fetch(`/api/evidence/files/${detailItemCode}`)
    //         .then(response => response.json())
    //         .then(files => {
    //             fileList.innerHTML = '';
    //             files.forEach(file => {
    //                 fileList.appendChild(renderFile(file));
    //             });
    //         });
    // }


    dropArea.addEventListener("dragenter", highlight, false);
    dropArea.addEventListener("dragover", highlight, false);
    dropArea.addEventListener("dragleave", unhighlight, false);
    dropArea.addEventListener("drop", handleDrop, false);

    return {
        handleFiles
        //getUploadedFiles
    };

}

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

const dropFile = new DropFile("drop-file", "files");

// Call this function when the modal is opened or the page is loaded to show existing files
//dropFile.getUploadedFiles();

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

//const storage = firebase.storage();
//const storageRef = firebase.storage().ref(fileName);


// 파일 업로드를 위한 AJAX 요청 추가
async function uploadFile(file, detailItemCode) {

    //firebase-storage에 파일 업로드
    const uploadTask = storageRef.put(file);
    uploadTask.on('state_changed', function(snapshot) {
        const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
        console.log('Upload is ' + progress + '% done');
        switch (snapshot.state) {
            case firebase.storage.TaskState.PAUSED: // or 'paused'
                console.log('Upload is paused');
                break;
            case firebase.storage.TaskState.RUNNING: // or 'running'
                console.log('Upload is running');
                break;
        }
    })

    let formData = new FormData();
    formData.append("file", file);
    //formData.append("detailItemCode", detailItemCode);

    let response = await fetch("/upload", {
        method: "POST",
        body: formData
    });

    if (response.ok) {
        displayFiles(detailItemCode);  // 업로드된 파일 목록을 갱신
    } else {
        console.error("파일 업로드 실패");
    }
}


//파일 업로드
document.getElementById("modal-button-proof").addEventListener("submit", async function(e) {
    e.preventDefault();

    const detailItemCode = button.getAttribute("data-detail-item-code") || "";

    document.getElementById("detailItemCode").value = detailItemCode;

    //const detailItemCode = document.querySelector('input[name="detailItemCode"]').value;
    const files = document.getElementById("chooseFile").files;

    for (let i = 0; i < files.length; i++) {
        await uploadFile(files[i], detailItemCode);
    }
});

//파일 조회
function displayFiles(detailItemCode) {
    fetch(`/files/${detailItemCode}`)
        .then(response => response.json())
        .then(async files => {
            const uploadedFilesDiv = document.getElementById("uploaded-files");
            uploadedFilesDiv.innerHTML = ''; // 기존 내용을 초기화

            for (const file of files) {

                const fileElement = document.createElement("div");
                fileElement.className = "file";

                // 파일 이름을 URL 인코딩하여 전달
                const encodedFileName = encodeURIComponent(file.name);

                fileElement.innerHTML = `
                    <div class="details">
                        <div class="header">
                            <span class="name">${file.name}</span>
                            <span class="size">${formatFileSize(file.size)}</span>
                            <button class="download-btn" onclick="downloadFile('/download?fileName=${encodedFileName}&detailItemCode=${detailItemCode}', '${file.name}')">download</button>
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

// function downloadFile(url, name) {
//
//     // 새로운 앵커 태그 생성
//     const link = document.createElement("a");
//     link.href = url;
//     link.download = name; // 파일명에서 앞의 11자 제거
//     document.body.appendChild(link);
//     link.click();
//     link.remove();
//
//     // window.open(url);
// }

// 모달에서 파일 정보 조회
function displayFilesInModal(detailItemCode) {
    // 서버에서 데이터 가져오기
    $.ajax({
        url: `/modal-files/${detailItemCode}`,
        method: 'GET',
        success: function(files) {
            // 기존의 파일 리스트를 초기화
            $('#uploaded-file').empty();

            if (files.length > 0) {
                // 파일 데이터를 순회하며 HTML에 삽입
                files.forEach(file => {
                    if (file.detailItemCode === detailItemCode) {  // 필터링 추가
                        const fileElement = `
                            <div class="file">
                                <div class="header">
                                    <span class="name">${file.fileName}</span>
                                    <span class="size">${formatFileSize(file.fileSize)}</span>
                                    <button class="delete-btn" onclick="confirmDelete('${file.fileName}', '${file.detailItemCode}')">X</button>
                                </div>
                            </div>
                        `;
                        $('#uploaded-file').append(fileElement);
                    }
                });
            } else {
                $('#uploaded-file').append('<p>No files found</p>');
            }
        },
        error: function(error) {
            console.error("Error fetching files:", error);
            $('#uploaded-file').append('<p>Error fetching files</p>');
        }
    });
}

// 파일 삭제
function confirmDelete(fileName, detailItemCode) {
    if (confirm(`${fileName}을(를) 정말 삭제하시겠습니까?`)) {

        const storage = getStorage();

        // Firebase Storage에서 파일을 가리키는 참조 생성
        const desertRef = ref(storage, `/${detailItemCode}/${fileName}`);

        deleteObject(desertRef).then(() => {
            deleteFileFromDB(fileName, detailItemCode);
        }).catch((error) => {
            console.error("Firebase 파일 삭제 실패:", error);
            alert("파일 삭제 중 오류가 발생했습니다.");
        });

        // var desertRef = storageRef.child('${detailItemCode}/${fileName}');
        // var desertRef = firebase.storage().ref(`${detailItemCode}/${fileName}`);

        // desertRef.delete().then(() => {
        //     // File deleted successfully
        //     deleteFileFromDB(fileName, detailItemCode);
        // }).catch((error) => {
        //     // Uh-oh, an error occurred!
        //     console.error("Firebase 파일 삭제 실패:", error);
        //     alert("파일 삭제 중 오류가 발생했습니다.");
        // });
    }
}

async function deleteFileFromDB(fileName, detailItemCode) {

    try {

        // 파일 정보 삭제를 위한 서버 요청
        let response = await fetch("/delete", {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (response.ok) {
            alert("파일이 성공적으로 삭제되었습니다.");
            displayFiles(detailItemCode); // 파일 목록 갱신
        } else {
            alert("파일 삭제에 실패했습니다.");
        }
    } catch (error) {
        console.error('Error:', error);
        alert("파일 삭제 중 오류가 발생했습니다.");
    }
}


