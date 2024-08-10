document.addEventListener("DOMContentLoaded", function() {
    var editButtons = document.querySelectorAll('.edit-btn');
    var saveButtons = document.querySelectorAll('.save-btn');

    var token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    var header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    editButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var documentCode = button.getAttribute('data-doc-code');
            console.log('Edit button clicked for document code:', documentCode); // 디버그 로그 추가
            var editButton = document.querySelector('.edit-btn[data-doc-code="' + documentCode + '"]');
            var saveButton = document.querySelector('.save-btn[data-doc-code="' + documentCode + '"]');
            var isoDetailsCell = document.getElementById('isoDetailsDiv-' + documentCode);
            var pciDssDetailsCell = document.getElementById('pciDssDiv-' + documentCode);
            var isoDetailsTextarea = document.getElementById('isoDetailsTextarea-' + documentCode);
            var pciDssDetailsTextarea = document.getElementById('pciDssTextarea-' + documentCode);

            var isoDetailsText = isoDetailsCell.innerText;
            var pciDssDetailsText = pciDssDetailsCell.innerText;

            isoDetailsTextarea.value = isoDetailsText;
            pciDssDetailsTextarea.value = pciDssDetailsText;
            isoDetailsTextarea.classList.remove('hidden');
            pciDssDetailsTextarea.classList.remove('hidden');
            isoDetailsCell.classList.add('hidden');
            pciDssDetailsCell.classList.add('hidden');

            editButton.classList.add('hidden');
            saveButton.classList.remove('hidden');
            console.log('Save button displayed for document code:', documentCode); // 디버그 로그 추가
        });
    });

    saveButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var documentCode = button.getAttribute('data-doc-code');
            console.log('Save button clicked for document code:', documentCode); // 디버그 로그 추가
            var editButton = document.querySelector('.edit-btn[data-doc-code="' + documentCode + '"]');
            var saveButton = document.querySelector('.save-btn[data-doc-code="' + documentCode + '"]');
            var isoDetailsTextarea = document.getElementById('isoDetailsTextarea-' + documentCode);
            var pciDssDetailsTextarea = document.getElementById('pciDssTextarea-' + documentCode);
            var isoDetails = isoDetailsTextarea.value;
            var pciDssDetails = pciDssDetailsTextarea.value;

            fetch('/save-details/' + documentCode, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token // CSRF 토큰 추가
                },
                body: JSON.stringify({
                    documentCode: documentCode,
                    isoDetails: isoDetails,
                    pciDssDetails: pciDssDetails
                }),
            })
                .then(response => {
                    console.log(response); // 응답 확인을 위한 로그
                    return response.json();
                })
                .then(data => {
                    console.log(data); // 응답 데이터 로그
                    if (data.success) {
                        var isoDetailsCell = document.getElementById('isoDetailsDiv-' + documentCode);
                        var pciDssDetailsCell = document.getElementById('pciDssDiv-' + documentCode);

                        isoDetailsCell.innerText = isoDetails;
                        pciDssDetailsCell.innerText = pciDssDetails;

                        isoDetailsCell.classList.remove('hidden');
                        pciDssDetailsCell.classList.remove('hidden');
                        isoDetailsTextarea.classList.add('hidden');
                        pciDssDetailsTextarea.classList.add('hidden');

                        editButton.classList.remove('hidden');
                        saveButton.classList.add('hidden');
                        console.log('Details saved and buttons toggled for document code:', documentCode); // 디버그 로그 추가
                    } else {
                        alert('Error saving details');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        });
    });
});
