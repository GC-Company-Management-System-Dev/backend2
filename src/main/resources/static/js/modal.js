// static/js/modal.js

// CSRF 토큰 설정
var token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
var header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// 작성 완료 체크 박스
document.addEventListener('DOMContentLoaded', (event) => {
    const checkbox = document.getElementById('status-completion');
    const detailItemCode = document.getElementById('detailItemCode').value;

    console.log(`Initial detailItemCode: ${detailItemCode}`);

    checkbox.addEventListener('change', () => {
        const completed = checkbox.checked;

        console.log(`Sending request to update completion status: detailItemCode=${detailItemCode}, completed=${completed}`);

        fetch(`/save-details/${detailItemCode}/update-completion-status`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify({
                detailItemCode: detailItemCode,
                completed: completed
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Status updated successfully');
                } else {
                    console.error('Error updating status:', data.message);
                }
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    });
});

// 모달 열기 함수
function openModal(modalId, buttonId) {
    var modal = document.getElementById(modalId);
    var button = document.getElementById(buttonId);

    if (modalId === 'editModal-certification') {
        var certificationStandard = button.getAttribute("data-certification-standard") || "";
        var majorPoints = button.getAttribute("data-major-points") || "";
        var relatedLaws = button.getAttribute("data-related-laws") || "";
        var modificationDate = button.getAttribute("data-modification-date") || "N/A";
        var modifier = button.getAttribute("data-modifier") || "N/A";

        document.getElementById("certificationStandard").value = certificationStandard;
        document.getElementById("majorPoints").value = majorPoints;
        document.getElementById("relatedLaws").value = relatedLaws;
        document.getElementById("modificationDate1").value = modificationDate;
        document.getElementById("modifier1").value = modifier;
    } else if (modalId === 'editModal-operational') {
        var status = button.getAttribute("data-status") || "";
        var relatedDocument = button.getAttribute("data-related-document") || "";
        var evidenceName = button.getAttribute("data-evidence-name") || "";
        var modificationDate = button.getAttribute("data-modification-date") || "N/A";
        var modifier = button.getAttribute("data-modifier") || "N/A";

        document.getElementById("currentSituation").value = status;
        document.getElementById("documentName").value = relatedDocument;
        document.getElementById("evidenceName").value = evidenceName;
        document.getElementById("modificationDate2").value = modificationDate;
        document.getElementById("modifier2").value = modifier;
    } else if (modalId === 'editModal-defects') {
        // 각 인증 구분에 맵핑된 데이터를 가져와 저장
        var ismsPDataElement = document.querySelector('[data-isms-p]');
        var iso27kDataElement = document.querySelector('[data-iso-27k]');
        var pciDssDataElement = document.querySelector('[data-pci-dss]');

        var ismsPData = ismsPDataElement ? ismsPDataElement.getAttribute('data-isms-p') : "";
        var iso27kData = iso27kDataElement ? iso27kDataElement.getAttribute('data-iso-27k') : "";
        var pciDssData = pciDssDataElement ? pciDssDataElement.getAttribute('data-pci-dss') : "";

        // 수정일과 수정자를 항상 가져옴
        var modificationDateElement = document.querySelector('[data-modification-date]');
        var modifierElement = document.querySelector('[data-modifier]');
        var modificationDate = modificationDateElement ? modificationDateElement.getAttribute('data-modification-date') : "N/A";
        var modifier = modifierElement ? modifierElement.getAttribute('data-modifier') : "N/A";


        // 인증 구분 선택 시 해당 데이터를 표시
        var certificationTypeSelect = document.getElementById('certificationType');
        certificationTypeSelect.addEventListener('change', function () {
            var selectedValue = this.value;
            var defectContentTextarea = document.getElementById('defectContent');

            if (selectedValue === 'ISMS-P') {
                defectContentTextarea.value = ismsPData;
            } else if (selectedValue === 'ISO 27K') {
                defectContentTextarea.value = iso27kData;
            } else if (selectedValue === 'PCI-DSS') {
                defectContentTextarea.value = pciDssData;
            }
        });

        // 초기화 (모달 열릴 때 기본 선택값에 따른 데이터 표시)
        certificationTypeSelect.dispatchEvent(new Event('change'));

        // 수정일과 수정자 설정
        document.getElementById("modificationDate4").value = modificationDate;
        document.getElementById("modifier4").value = modifier;
    } else if (modalId === 'editModal-proof') {

        document.getElementById(modalId).style.display = "block";
        const detailItemCode = document.getElementById("detailItemCode").value;
        displayFilesInModal(detailItemCode); // 모달을 열 때 업로드된 파일 정보 로드

        // 변경 일시와 변경자 정보 가져오기
        fetch(`/evidence-modification-info/${detailItemCode}`)
            .then(response => response.json())
            .then(info => {
                document.getElementById("modificationDate3").value = new Date(info.lastModified).toLocaleString();
                document.getElementById("modifier3").value = info.creator;
            })
            .catch(error => {
                console.error("정보를 가져오는 중 오류 발생:", error);
            });

        // var detailItemCode = button.getAttribute("data-detail-item-code") || "";
        // var modificationDate = button.getAttribute("data-modification-date") || "N/A";
        // var modifier = button.getAttribute("data-modifier") || "N/A";
        //
        // document.getElementById("detailItemCode").value = detailItemCode;
        // document.getElementById("modificationDate3").value = modificationDate;
        // document.getElementById("modifier3").value = modifier;
    }

    modal.style.display = "block";
}


// 모달 닫기 함수
function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    modal.style.display = "none";
    location.reload(); // 페이지를 새로고침하여 수정된 데이터를 반영
}

// 수정 내용 처리 함수
// 인증 항목 내용 수정 처리 함수
function saveChangesCertification(button) {

    var detailItemCode = button.getAttribute("data-detail-item-code") || "";

    document.getElementById("detailItemCode").value = detailItemCode;

    var certificationStandard = document.getElementById("certificationStandard").value.trim();
    var majorPoints = document.getElementById("majorPoints").value.trim();
    var relatedLaws = document.getElementById("relatedLaws").value.trim();
    var modifier1 = document.getElementById("modifier1").value;

    if (certificationStandard === "" && majorPoints === "" && relatedLaws === "") {
        alert("빈 값으로 저장할 수 없습니다.");
        return;
    }

    fetch(`/save-details/${detailItemCode}/update-certifContent`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify({
            detailItemCode: detailItemCode,
            certificationCriteria: certificationStandard,
            keyCheckpoints: majorPoints,
            relevantLaws: relatedLaws,
            modifier: modifier1
        }),
    })
        .then(response => response.json())
        .then(data => {
            if (data.success){

                // 데이터를 업데이트하는 로직 (예: 테이블의 셀 값 업데이트)
                var rows = document.querySelectorAll('[data-detail-item-code="' + detailItemCode + '"]');
                rows.forEach(function(row) {
                    if (row.querySelector('[data-certification-standard]')) {
                        row.querySelector('[data-certification-standard]').innerText = certificationStandard;
                    }
                    if (row.querySelector('[data-major-points]')) {
                        row.querySelector('[data-major-points]').innerText = majorPoints;
                    }
                    if (row.querySelector('[data-related-laws]')) {
                        row.querySelector('[data-related-laws]').innerText = relatedLaws;
                    }
                });

            } else {
                alert('Error updating certifcontent: ' + (body.message || 'Unknown error'));
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error updating certifcontent: ' + error.message);
        });

    closeModal('editModal-certification');
    location.reload(); // 페이지를 새로고침하여 수정된 데이터를 반영
}

// 운영 현황 수정 처리 함수
function saveChangesOperational(button) {

    var detailItemCode = button.getAttribute("data-detail-item-code") || "";

    document.getElementById("detailItemCode").value = detailItemCode;

    var currentSituation = document.getElementById("currentSituation").value.trim();
    var documentName = document.getElementById("documentName").value.trim();
    var evidenceName = document.getElementById("evidenceName").value.trim();
    var modifier2 = document.getElementById("modifier2").value;

    if (currentSituation === "" && documentName === "" && evidenceName === "") {
        alert("빈 값으로 저장할 수 없습니다.");
        return;
    }

    fetch(`/save-details/${detailItemCode}/update-operationalStatus`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify({
            detailItemCode: detailItemCode,
            status: currentSituation,
            relatedDocument: documentName,
            evidenceName: evidenceName,
            modifier: modifier2
        }),
    })
        .then(response => response.json())
        .then(data => {
            if (data.success){

                // 데이터를 업데이트하는 로직 (예: 테이블의 셀 값 업데이트)
                var rows = document.querySelectorAll('[data-detail-item-code="' + detailItemCode + '"]');
                rows.forEach(function(row) {
                    if (row.querySelector('[data-status]')) {
                        row.querySelector('[data-status]').innerText = currentSituation;
                    }
                    if (row.querySelector('[data-related-document]')) {
                        row.querySelector('[data-related-document]').innerText = documentName;
                    }
                    if (row.querySelector('[data-evidence-name]')) {
                        row.querySelector('[data-evidence-name]').innerText = evidenceName;
                    }
                });

            } else {
                alert('Error updating operational status: ' + (body.message || 'Unknown error'));
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error updating operational status: ' + error.message);
        });

    closeModal('editModal-operational');
    location.reload(); // 페이지를 새로고침하여 수정된 데이터를 반영
}

// 결함 관리 수정 처리 함수
function saveChangesDefects(button) {
    var detailItemCode = button.getAttribute("data-detail-item-code") || "";
    var modifier4 = document.getElementById("modifier4").value;
    var certificationType = document.getElementById('certificationType').value;
    var defectContent = document.getElementById('defectContent').value.trim();

    var ismsP = certificationType === 'ISMS-P' ? defectContent : "";
    var iso27k = certificationType === 'ISO 27K' ? defectContent : "";
    var pciDss = certificationType === 'PCI-DSS' ? defectContent : "";

    if (!defectContent) { // defectContent가 null 또는 빈 문자열인 경우
        alert("내용을 입력해야 합니다.");
        return;
    }

    fetch(`/save-details/${detailItemCode}/update-defectManage`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify({
            detailItemCode: detailItemCode,
            certificationType: certificationType,
            defectContent: defectContent,
            ismsP: ismsP,
            iso27k: iso27k,
            pciDss: pciDss,
            modifier: modifier4
        }),
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                var columns = document.querySelectorAll('[data-detail-item-code="' + detailItemCode + '"]');
                columns.forEach(function(column) {
                    if (certificationType === 'ISMS-P' && column.getAttribute('data-isms-p') !== null) {
                        column.setAttribute('data-isms-p', ismsP);
                        column.innerText = ismsP;
                    }
                    if (certificationType === 'ISO 27K' && column.getAttribute('data-iso-27k') !== null) {
                        column.setAttribute('data-iso-27k', iso27k);
                        column.innerText = iso27k;
                    }
                    if (certificationType === 'PCI-DSS' && column.getAttribute('data-pci-dss') !== null) {
                        column.setAttribute('data-pci-dss', pciDss);
                        column.innerText = pciDss;
                    }
                });
            } else {
                alert('Error updating defectmanage: ' + (data.message || 'Unknown error'));
            }
        })
        .then(() => { location.reload() })// 페이지를 새로고침하여 수정된 데이터를 반영)
        .catch(error => {
            console.error('Error:', error);
            alert('Error updating defectmanage: ' + error.message);
        });

    closeModal('editModal-defects');

}



//main
$(function() {
    $(".search-button").hover(
        function() {
            // 마우스 오버 시
            $(this).css({
                "border-color": "white",
                "color": "white",
                "background-color": "red"
            });
        },
        function() {
            // 마우스 아웃 시
            $(this).css({
                "border-color": "red",
                "color": "red",
                "background-color": "transparent"
            });
        }
    );
});


$(function () {
    $("#datepicker").datepicker({
        changeMonth: false, // 월 선택 비활성화
        changeDay: false, // 일 선택 비활성화
        changeYear: true, // 연도 선택 활성화
        showButtonPanel: true, // 버튼 패널 표시
        dateFormat: 'yy', // 연도 형식 설정
        yearRange: "c-10:c+10", // 연도 범위 설정 (현재 연도 기준 -10년에서 +10년)
        onClose: function(dateText, inst) {
            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            $(this).val(year);
        },
        beforeShow: function(input, inst) {
            if ((datestr = $(this).val()).length > 0) {
                year = datestr.substring(datestr.length - 4, datestr.length);
                $(this).datepicker('option', 'defaultDate', new Date(year, 1, 1));
                $(this).datepicker('setDate', new Date(year, 1, 1));
            }
            $(this).datepicker('widget').addClass('hide-calendar');
            $(".ui-datepicker-calendar").hide();
            $(".ui-datepicker-month").hide();
        }
    });

    // Add custom style to hide calendar and month selector
    $("<style>")
        .prop("type", "text/css")
        .html("\
        .hide-calendar .ui-datepicker-calendar, .hide-calendar .ui-datepicker-month { display: none !important; }")
        .appendTo("head");
});

$(function () {
    $("#search-button").click(function () {
        $("#datepicker").datepicker("show");
    });
});

$(document).ready(function() {
    $("#reset-year-button").click(function() {
        $("#firstModal").css("display", "block");
    });

    $(".close-main").click(function() {
        $(".modal-main").css("display", "none");
    });

    $("#firstModal .modal-button-main").click(function() {
        $("#firstModal").css("display", "none");
        $("#secondModal").css("display", "block");
    });

    $("#secondModal .proceed-button").click(function() {
        alert("초기화 되었습니다.");
        $(".modal-main").css("display", "none");
    });

    $("#secondModal .cancel-button").click(function() {
        $(".modal-main").css("display", "none");
    });
});

