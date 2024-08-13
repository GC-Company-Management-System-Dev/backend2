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

    function deleteFile(fileDOM, fileKey) {
        if (fileKey) {
            // 서버에서 파일 삭제 요청
            fetch(`/api/evidence/${fileKey}`, {
                method: 'DELETE'
            }).then(() => {
                fileDOM.remove(); // 성공 시 DOM에서 제거
            }).catch(error => {
                console.error('Error:', error);
            });
        } else {
            // 서버에 업로드되지 않은 경우, 단순히 DOM에서만 제거
            fileDOM.remove();
        }
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

        // 새로운 변수 추가: fileKey는 서버에서 받은 경우에만 사용됨
        let fileKey = file.key || null;

        fileDOM.className = "file";
        fileDOM.innerHTML = `
        <div class="details">
            <div class="header">
                <span class="name">${file.name}</span>
                <span class="size">${formatFileSize(file.size)}</span>
                <button class="delete-btn" onclick="deleteFile(this.parentNode.parentNode.parentNode, ${fileKey ? `'${fileKey}'` : null})">X</button>
<!--                <a href="/download?fileName=${file.name}&detailItemCode=${detailItemCode}" class="download-btn">Download</a> &lt;!&ndash; 다운로드 버튼 추가 &ndash;&gt;-->
            </div>
        </div>
    `;


        // 파일 엘리먼트를 업로드된 파일 목록에 추가
        const uploadedFiles = document.getElementById("uploaded-files");
        uploadedFiles.appendChild(fileDOM);

        // 삭제 버튼에 이벤트 리스너 연결
        fileDOM.querySelector('.delete-btn').addEventListener('click', function() {
            deleteFile(fileDOM, fileKey);
        });


        return fileDOM;
    }


    function getUploadedFiles() {
        fetch(`/api/evidence/files/${detailItemCode}`)
            .then(response => response.json())
            .then(files => {
                fileList.innerHTML = '';
                files.forEach(file => {
                    fileList.appendChild(renderFile(file));
                });
            });
    }


    dropArea.addEventListener("dragenter", highlight, false);
    dropArea.addEventListener("dragover", highlight, false);
    dropArea.addEventListener("dragleave", unhighlight, false);
    dropArea.addEventListener("drop", handleDrop, false);

    return {
        handleFiles,
        getUploadedFiles
    };

}

const dropFile = new DropFile("drop-file", "files");

// Call this function when the modal is opened or the page is loaded to show existing files
dropFile.getUploadedFiles();

var firebaseConfig = {
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

var auth = firebase.auth();
var user = firebase.auth().currentUser;
var provider = new firebase.auth.GoogleAuthProvider();
var storage = firebase.storage();

//firebase-storage에 파일 업로드
var storageRef = firebase.storage().ref(fileName);
var uploadTask = storageRef.put(file);
uploadTask.on('state_changed', function(snapshot) {
    var progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
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


// 파일 업로드를 위한 AJAX 요청 추가
async function uploadFile(file, detailItemCode) {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("detailItemCode", detailItemCode);

    let response = await fetch("/upload", {
        method: "POST",
        body: formData
    });

    if (response.ok) {
        // 파일이 업로드된 후 메타데이터에서 액세스 토큰을 가져오기
        let result = await response.json();
        const fileName = result.fileName; // 서버에서 반환된 파일 이름 (또는 키)
        let fileMetadata = await getFileMetadata(fileName);

        if (!fileMetadata.firebaseStorageDownloadTokens) {
            // 액세스 토큰이 없으면 생성 트리거 (필요에 따라 서버 또는 Firebase 설정에 따라 다름)
            await generateAccessToken(fileName);
            // 새로 생성된 메타데이터를 다시 가져오기
            fileMetadata = await getFileMetadata(fileName);
        }

        displayFiles(detailItemCode);  // 업로드된 파일 목록을 갱신
    } else {
        console.error("파일 업로드 실패");
    }

    //return await response.json();
}

// Firebase에서 파일의 메타데이터를 가져오는 함수
async function getFileMetadata(fileName) {
    const response = await fetch(`/api/files/metadata/${fileName}`);
    return await response.json();
}

// 파일에 대한 액세스 토큰을 생성하는 트리거 함수
async function generateAccessToken(fileName) {
    const response = await fetch(`/api/files/generate-token/${fileName}`, {
        method: 'POST'
    });
    return await response.json();
}

//파일 업로드
document.getElementById("modal-button-proof").addEventListener("submit", async function(e) {
    e.preventDefault();

    var detailItemCode = button.getAttribute("data-detail-item-code") || "";

    document.getElementById("detailItemCode").value = detailItemCode;

    //const detailItemCode = document.querySelector('input[name="detailItemCode"]').value;
    const files = document.getElementById("chooseFile").files;

    let formData = new FormData();
    formData.append("detailItemCode", detailItemCode);

    for (let i = 0; i < files.length; i++) {
        formData.append("file", files[i]);
    }

    let response = await fetch("/upload", {
        method: "POST",
        body: formData
    });

    if (response.ok) {
        let result = await response.json();
        // 성공 시 처리
        displayFiles(detailItemCode);  // 업로드된 파일 목록을 갱신
        // dropFile.getUploadedFiles();  // 업로드된 파일 목록을 갱신
    } else {
        // 실패 시 처리
        console.error("파일 업로드 실패");
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

                // 메타데이터에서 액세스 토큰을 가져와서 다운로드 링크 생성
                const fileMetadata = await getFileMetadata(file.name);
                const accessToken = fileMetadata.firebaseStorageDownloadTokens;
                const downloadUrl = `${file.url}&token=${accessToken}`;

                fileElement.innerHTML = `
                    <div class="details">
                        <div class="header">
                            <span class="name">${file.name}</span>
                            <span class="size">${formatFileSize(file.size)}</span>
                            <button class="download-btn" onclick="downloadFile('${downloadUrl}')">Download</button>
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
function downloadFile(url) {
    window.open(url, '_blank');
}

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
                    const fileElement = `
                        <div class="file">
                            <div class="header">
                                <span class="name">${file.fileName}</span>
                                <span class="size">${file.fileSize} bytes</span>
                                <button class="delete-btn" onclick="confirmDelete('${file.fileName}', '${file.fileSize}', '${file.detailItemCode}')">X</button>
                            </div>
                        </div>
                    `;
                    $('#uploaded-file').append(fileElement);
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
function confirmDelete(fileName, fileSize, detailItemCode) {
    if (confirm(`${fileName}을(를) 정말 삭제하시겠습니까?`)) {

        // const storage = getStorage();
        //
        // // Firebase Storage에서 파일을 가리키는 참조 생성
        // const desertRef = ref(storage, `${detailItemCode}/${fileName}`);
        //
        // deleteObject(desertRef).then(() => {
        //     // File deleted successfully
        // }).catch((error) => {
        //     // Uh-oh, an error occurred!
        // });

        var desertRef = storageRef.child('${detailItemCode}/${fileName}');

        desertRef.delete().then(() => {
            // File deleted successfully
        }).catch((error) => {
            // Uh-oh, an error occurred!
        });

        deleteFile(fileName, fileSize, detailItemCode);
    }
}

function deleteFile(fileName, fileSize, detailItemCode) {

    try {

        // 파일 정보 삭제를 위한 서버 요청
        fetch(`/delete/${detailItemCode}/${fileName}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                fileName: fileName,
                fileSize: fileSize,
                detailItemCode: detailItemCode
            })
        });

        if (response.ok) {
            alert("파일이 성공적으로 삭제되었습니다.");
            dropFile.getUploadedFiles(); // 파일 목록 갱신
        } else {
            alert("파일 삭제에 실패했습니다.");
        }
    } catch (error) {
        console.error('Error:', error);
        alert("파일 삭제 중 오류가 발생했습니다.");
    }
}


// // 파일 삭제 함수
// function deleteFile(fileName, detailItemCode) {
//     if (confirm("이 파일을 삭제하시겠습니까?")) {
//         fetch(`/delete-file`, {
//             method: "POST",
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify({ fileName: fileName, detailItemCode: detailItemCode })
//         })
//             .then(response => {
//                 if (response.ok) {
//                     // 파일 삭제 후 목록 갱신
//                     displayFiles(detailItemCode);
//                 } else {
//                     console.error("파일 삭제 실패");
//                 }
//             })
//             .catch(error => {
//                 console.error("파일 삭제 중 오류 발생:", error);
//             });
//     }
// }

// function openModal(modalId, buttonId) {
//     var modal = document.getElementById(modalId);
//     var button = document.getElementById(buttonId);
//
//     if (modalId === 'editModal-proof') {
//         document.getElementById(modalId).style.display = "block";
//         const detailItemCode = document.getElementById("detailItemCode").value;
//         displayFiles(detailItemCode);  // 모달 열릴 때 파일 목록 갱신
//         displayFilesInModal(detailItemCode); // 모달을 열 때 업로드된 파일 정보 로드
//
//         // 변경 일시와 변경자 정보 가져오기
//         fetch(`/evidence-modification-info/${detailItemCode}`)
//             .then(response => response.json())
//             .then(info => {
//                 document.getElementById("modificationDate3").value = new Date(info.lastModified).toLocaleString();
//                 document.getElementById("modifier3").value = info.creator;
//             })
//             .catch(error => {
//                 console.error("정보를 가져오는 중 오류 발생:", error);
//             });
//
//         // var detailItemCode = button.getAttribute("data-detail-item-code") || "";
//         // var modificationDate = button.getAttribute("data-modification-date") || "N/A";
//         // var modifier = button.getAttribute("data-modifier") || "N/A";
//         //
//         // document.getElementById("detailItemCode").value = detailItemCode;
//         // document.getElementById("modificationDate3").value = modificationDate;
//         // document.getElementById("modifier3").value = modifier;
//     }
//
//     modal.style.display = "block";
// }

// async function downloadFile(fileName) {
//     console.log("downloadFile 함수 호출됨, 파일 이름:", fileName);
//
//     const storage = getStorage();
//
//     // Firebase Storage 참조
//     //var storage = firebase.storage();
//
//     // Firebase Storage에서 파일 참조
//     const storageRef = storage.ref(fileName);
//
//     // try {
//     //     // 다운로드 URL 가져오기
//     //     const url = await storageRef.getDownloadURL();
//     //
//     //     // URL을 사용하여 새 창에서 파일 다운로드
//     //     window.open(url, '_blank');
//     // } catch (error) {
//     //     console.error("파일 다운로드 실패:", error);
//     // }
//
//     // // 파일의 다운로드 URL 가져오기
//     // storageRef.getDownloadURL()
//     //     .then(function(url) {
//     //         console.log("다운로드 URL 생성됨:", url);
//     //         // URL을 사용하여 파일 다운로드
//     //         window.open(url, '_blank');
//     //     })
//     //     .catch(function(error) {
//     //         console.error("파일 다운로드 실패: ", error);
//     //     });
// }



// async function downloadFile(filePath) {
//     // Firebase Storage의 파일 참조를 가져옴
//     var storageRef = firebase.storage().ref(filePath);
//
//     try {
//         // 다운로드 가능한 URL을 가져옴 (액세스 토큰 포함)
//         let downloadURL = await storageRef.getDownloadURL();
//
//         // 다운로드 링크를 통해 파일을 다운로드
//         window.open(downloadURL, '_blank');
//     } catch (error) {
//         console.error("Error getting download URL:", error);
//     }
// }



// //업로드한 파일 목록 조회
//
//
// function loadFiles(detailItemCode) {
//     // detailItemCode 폴더의 참조를 가져옵니다.
//     var folderRef = storageRef.child(`detailItemCode/${detailItemCode}`);
//
//     // 폴더에 있는 모든 파일 목록을 가져옵니다.
//     folderRef.listAll().then(function(result) {
//         result.items.forEach(function(fileRef) {
//             // 각 파일의 메타데이터를 가져옵니다.
//             fileRef.getMetadata().then(function(metadata) {
//                 // 파일 이름과 크기를 사용하여 버튼을 만듭니다.
//                 var button = document.createElement('button');
//                 button.textContent = `파일 이름: ${metadata.name}, 크기: ${metadata.size} bytes`;
//                 button.classList.add('file-button'); // 버튼 스타일을 위해 클래스 추가
//
//                 // 섹션 4에 버튼을 추가합니다.
//                 document.getElementById('uploaded-files').appendChild(button);
//             }).catch(function(error) {
//                 console.log("파일 메타데이터를 가져오는 중 오류 발생: ", error);
//             });
//         });
//     }).catch(function(error) {
//         console.log("폴더 내 파일 목록을 가져오는 중 오류 발생: ", error);
//     });
// }
//
// // 문서가 로드될 때 loadFiles 함수를 호출합니다.
// document.addEventListener('DOMContentLoaded', function(detailItemCode) {
//     loadFiles(detailItemCode);
// });


// async function getFilesList(detailItemCode) {
//     const storageRef = firebase.storage().ref(detailItemCode);
//     const fileList = await storageRef.listAll();
//     const files = fileList.items.map(async (itemRef) => {
//         const fileURL = await itemRef.getDownloadURL();
//         const metadata = await itemRef.getMetadata();
//         return {
//             name: itemRef.name,
//             size: metadata.size,
//             url: fileURL,
//             fullPath: itemRef.fullPath
//         };
//     });
//
//     return Promise.all(files);
// }
//
// async function displayFiles(detailItemCode) {
//     const files = await getFilesList(detailItemCode);
//     const uploadedFilesSection = document.getElementById("uploaded-files");
//     const modalFilesSection = document.getElementById("files");
//
//     uploadedFilesSection.innerHTML = '';  // 기존 파일 목록 초기화
//     modalFilesSection.innerHTML = '';
//
//     files.forEach(file => {
//         const fileDOM = document.createElement("div");
//         fileDOM.className = "file";
//         fileDOM.innerHTML = `
//             <div class="details">
//                 <div class="header">
//                     <span class="name">${file.name}</span>
//                     <span class="size">${(file.size / 1024).toFixed(2)} KB</span>
//                     <a href="${file.url}" target="_blank" class="download-link">다운로드</a>
//                 </div>
//             </div>
//         `;
//
//         uploadedFilesSection.appendChild(fileDOM);
//         modalFilesSection.appendChild(fileDOM.cloneNode(true));  // 모달에도 동일하게 추가
//     });
// }

// document.addEventListener('DOMContentLoaded', () => {
//     const detailItemCode = document.getElementById("detailItemCode").value;
//     displayFiles(detailItemCode);
// });
