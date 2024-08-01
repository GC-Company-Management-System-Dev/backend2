// static/js/modal.js

// 모달 열기 함수
function openModal(modalId, buttonId) {
    var modal = document.getElementById(modalId);
    var button = document.getElementById(buttonId);

    if (modalId === 'editModal-certification') {
        var detailItemCode = button.getAttribute("data-detail-item-code") || "";
        var certificationStandard = button.getAttribute("data-certification-standard") || "";
        var majorPoints = button.getAttribute("data-major-points") || "";
        var relatedLaws = button.getAttribute("data-related-laws") || "";
        var modificationDate = button.getAttribute("data-modification-date") || "N/A";
        var modifier = button.getAttribute("data-modifier") || "N/A";

        document.getElementById("detailItemCode").value = detailItemCode;
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
    }

    modal.style.display = "block";
}

function setModalData(row, modalId) {
    if (modalId === 'editModal-defects') {
        var ismsP = row.getAttribute("data-isms-p");
        var iso27k = row.getAttribute("data-iso-27k");
        var pciDss = row.getAttribute("data-pci-dss");
        var modificationDate = row.getAttribute("data-modification-date");
        var modifier = row.getAttribute("data-modifier");

        document.getElementById("certificationType").value = ismsP;
        document.getElementById("defectContent").value = iso27k;
        document.getElementById("pciDss").value = pciDss;
        document.getElementById("modificationDate4").value = modificationDate;
        document.getElementById("modifier4").value = modifier;
    }

    document.getElementById(modalId).style.display = "block";
}

// 모달 닫기 함수
function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    modal.style.display = "none";
}

// 수정 내용 처리 함수
// 인증 항목 내용 수정 처리 함수
function saveChangesCertification() {
    var certificationStandard = document.getElementById("certificationStandard").value;
    var majorPoints = document.getElementById("majorPoints").value;
    var relatedLaws = document.getElementById("relatedLaws").value;

    console.log("인증기준:", certificationStandard);
    console.log("주요 확인사항:", majorPoints);
    console.log("관련 법규:", relatedLaws);

    var currentDate = new Date();
    var formattedDate = currentDate.toLocaleString();

    document.getElementById("modificationDate1").value = formattedDate;
    document.getElementById("modifier1").value = "사용자";

    closeModal('editModal-certification');
}

// 운영 현황 수정 처리 함수
function saveChangesOperational() {
    var currentSituation = document.getElementById("currentSituation").value;
    var documentName = document.getElementById("documentName").value;
    var evidenceName = document.getElementById("evidenceName").value;

    console.log("현황:", currentSituation);
    console.log("문서명:", documentName);
    console.log("증적명:", evidenceName);

    var currentDate = new Date();
    var formattedDate = currentDate.toLocaleString();

    document.getElementById("modificationDate2").value = formattedDate;
    document.getElementById("modifier2").value = "사용자";

    closeModal('editModal-operational');
}

// 증적자료 업로드 처리 함수
function saveChangesProofUpload() {
    var proofFiles = document.getElementById("proofFile").files;

    console.log("업로드한 파일 수:", proofFiles.length);
    // 파일 업로드 및 처리 로직을 여기에 추가
    // 모달의 등록하기 버튼 클릭 시 파일 업로드를 처리
    document.getElementById("modal-button-proof").addEventListener("click", function(event) {
        event.preventDefault();
        const files = document.getElementById("chooseFile").files;
        handleFiles(files);
        closeModal('editModal-proof');
    });

    var currentDate = new Date();
    var formattedDate = currentDate.toLocaleString();

    document.getElementById("modificationDate3").value = formattedDate;
    document.getElementById("modifier3").value = "사용자";

    closeModal('editModal-proof');
}

// 결함 관리 수정 처리 함수
function saveChangesDefects() {
    var certificationType = document.getElementById("certificationType").value;
    var defectContent = document.getElementById("defectContent").value;

    console.log("인증 구분:", certificationType);
    console.log("결함 내용:", defectContent);

    var currentDate = new Date();
    var formattedDate = currentDate.toLocaleString();

    document.getElementById("modificationDate4").value = formattedDate;
    document.getElementById("modifier4").value = "사용자";

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