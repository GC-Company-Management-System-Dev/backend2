package com.yeogi.scms.controller;

import com.google.api.Authentication;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.remoteconfig.User;
import com.yeogi.scms.domain.*;
import com.yeogi.scms.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final CertifContentService certifContentService;
    private final DefectManageService defectManageService;
    private final OperationalStatusService operationalStatusService;
    private final SCMasterService scmMasterService;
    private final CertifDetailService certifDetailService;
    private final LoginAccountService loginAccountService;
    private final AccessLogService accessLogService;

    @Autowired
    private EvidenceDataService evidenceDataService;

    private final AuthenticationService authenticationService;
    private final Path root = Paths.get("uploads");

    private Log logger;

    @Autowired
    public MainController(SCMasterService scmMasterService, CertifDetailService certifDetailService, CertifContentService certifContentService,
                          DefectManageService defectManageService, OperationalStatusService operationalStatusService, EvidenceDataService evidenceDataService,
                          LoginAccountService loginAccountService, AccessLogService accessLogService, AuthenticationService authenticationService) {
        this.scmMasterService = scmMasterService;
        this.certifDetailService = certifDetailService;
        this.certifContentService = certifContentService;
        this.defectManageService = defectManageService;
        this.operationalStatusService = operationalStatusService;
        this.evidenceDataService = evidenceDataService;
        this.loginAccountService = loginAccountService;
        this.accessLogService = accessLogService;
        this.authenticationService = authenticationService;
    }

    private void addNicknameToModel(Model model, CustomUserDetails user) {
        String nickname = (user != null) ? user.getNickname() : "Guest";
        model.addAttribute("nickname", nickname);
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid id or password");
        }
        return "login";
    }

    @PostMapping("/verify-password")
    @ResponseBody
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request, @AuthenticationPrincipal CustomUserDetails user) {
        String password = request.get("password");

        boolean isPasswordValid = authenticationService.verifyPassword(user.getUsername(), password);
        if (isPasswordValid) {
            return ResponseEntity.ok("Password verified successfully");
        } else {
            return ResponseEntity.status(401).body("Invalid password");
        }
    }

    @GetMapping("/manage-system")
    public String showManageSystem(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);
        // (1) SCMasterService.getFilteredSCMaster("MANAGE") 호출
        List<SCMaster> scmMasters = scmMasterService.getSCMasterBySCCode("MANAGE");

        // (2) 각 SCMaster의 documentCode에 맵핑되는 CertifDetail만 가져옴
        Map<String, List<CertifDetail>> certifDetailsMap = scmMasters.stream()
                .collect(Collectors.toMap(
                        SCMaster::getDocumentCode,
                        master -> certifDetailService.getCertifDetailByDoccode(master.getDocumentCode())
                ));

        // 모델에 추가
        model.addAttribute("scmMasters", scmMasters);
        model.addAttribute("certifDetails", certifDetailsMap);

        return "manageSystem";
    }

    @GetMapping("/protect-measures")
    public String showProtectMeasures(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);

        List<SCMaster> scmMasters = scmMasterService.getSCMasterBySCCode("PROTECT");

        Map<String, List<CertifDetail>> certifDetailsMap = scmMasters.stream()
                .collect(Collectors.toMap(
                        SCMaster::getDocumentCode,
                        master -> certifDetailService.getCertifDetailByDoccode(master.getDocumentCode())
                ));

        model.addAttribute("scmMasters", scmMasters);
        model.addAttribute("certifDetails", certifDetailsMap);

        return "protectMeasures";
    }

    @GetMapping("/privacy-require")
    public String showPrivacyRequire(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);
        List<SCMaster> scmMasters = scmMasterService.getSCMasterBySCCode("PRIVACY");

        Map<String, List<CertifDetail>> certifDetailsMap = scmMasters.stream()
                .collect(Collectors.toMap(
                        SCMaster::getDocumentCode,
                        master -> certifDetailService.getCertifDetailByDoccode(master.getDocumentCode())
                ));

        model.addAttribute("scmMasters", scmMasters);
        model.addAttribute("certifDetails", certifDetailsMap);

        return "privacyRequire";
    }

    @GetMapping("/manage-system/{detailItemCode}")
    public String showManageSystemDetail(@PathVariable String detailItemCode, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        return showDetail(detailItemCode, "manage-system", model, user);
    }

    @GetMapping("/protect-measures/{detailItemCode}")
    public String showProtectMeasuresDetail(@PathVariable String detailItemCode, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        return showDetail(detailItemCode, "protect-measures", model, user);
    }

    @GetMapping("/privacy-require/{detailItemCode}")
    public String showPrivacyRequireDetail(@PathVariable String detailItemCode, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        return showDetail(detailItemCode, "privacy-require", model, user);
    }

    public String showDetail(String detailItemCode, String from, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);

        List<CertifDetail> details = certifDetailService.getCertifDetailByDCode(detailItemCode);
        List<CertifContent> certifContents = certifContentService.getCertifContentByDCode(detailItemCode);
        List<DefectManage> defectManages = defectManageService.getDefectManageByDCode(detailItemCode);
        List<OperationalStatus> operationalStatuses = operationalStatusService.getOperationalStatusByDCode(detailItemCode);
        List<EvidenceData> evidenceDatas = evidenceDataService.getEvidenceDataByDCode(detailItemCode);

        String detailItemTypeName = details.stream()
                .filter(detail -> detailItemCode.equals(detail.getDetailItemCode()))
                .map(CertifDetail::getDetailItemTypeName)
                .findFirst()
                .orElse("");

        boolean completed = details.stream()
                .filter(detail -> detailItemCode.equals(detail.getDetailItemCode()))
                .map(CertifDetail::isCompleted)
                .findFirst()
                .orElse(false);

        model.addAttribute("detailItemTypeName", detailItemTypeName);
        model.addAttribute("certifContents", certifContents);
        model.addAttribute("defectManages", defectManages);
        model.addAttribute("operationalStatuses", operationalStatuses);
        model.addAttribute("evidenceDatas", evidenceDatas);
        model.addAttribute("from", from);
        model.addAttribute("detailItemCode", detailItemCode); // 여기 추가
        model.addAttribute("completed", completed);

        return "detail-template";
    }


    @GetMapping("/log-manage")
    public String showLogs(Model model, @AuthenticationPrincipal CustomUserDetails user,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size) {
        addNicknameToModel(model, user);
        int totalLogs = accessLogService.getTotalLogCount();
        List<AccessLog> logs = accessLogService.getAllLogs(page, size);
        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalLogs / size));
        return "logManage";
    }


    @PostMapping("/save-details/{detailItemCode}")
    public ResponseEntity<?> saveDetails(@PathVariable String detailItemCode, @RequestBody Map<String, String> details) {
        String documentCode = details.get("documentCode");
        String isoDetails = details.get("isoDetails");
        String pciDssDetails = details.get("pciDssDetails");

        // Use detailItemCode in your business logic if needed
        boolean success = scmMasterService.saveDetailsToDB(documentCode, isoDetails, pciDssDetails);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error saving details"));
        }
    }

    @PostMapping("/save-details/{detailItemCode}/update-completion-status")
    public ResponseEntity<?> updateCompletionStatus(@PathVariable String detailItemCode, @RequestBody Map<String, Object> payload) {
        detailItemCode = (String) payload.get("detailItemCode");
        Boolean completed = (Boolean) payload.get("completed");

        System.out.println("Received request to update completion status: detailItemCode=" + detailItemCode + "completed=" + completed);

        boolean success = certifDetailService.updateCompletionStatus(detailItemCode, completed);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error updating completion status"));
        }
    }

    @PostMapping("/save-details/{detailItemCode}/update-certifContent")
    public ResponseEntity<?> saveCertifContent(@PathVariable String detailItemCode, @RequestBody Map<String, String> certifContent) {
        detailItemCode = certifContent.get("detailItemCode");
        String certificationCriteria = certifContent.get("certificationCriteria");
        String keyCheckpoints = certifContent.get("keyCheckpoints");
        String relevantLaws = certifContent.get("relevantLaws");
        String modifier = certifContent.get("modifier");

        boolean success = certifContentService.updateCertifContent(detailItemCode, certificationCriteria, keyCheckpoints, relevantLaws, modifier);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error updating certification content"));
        }
    }

    @PostMapping("/save-details/{detailItemCode}/update-operationalStatus")
    public ResponseEntity<?> saveOperationalStatus(@PathVariable String detailItemCode, @RequestBody Map<String, String> operationalStatus) {
        detailItemCode = operationalStatus.get("detailItemCode");
        String status = operationalStatus.get("status");
        String relatedDocument = operationalStatus.get("relatedDocument");
        String evidenceName = operationalStatus.get("evidenceName");
        String modifier = operationalStatus.get("modifier");

        boolean success = operationalStatusService.updateOperationalStatus(detailItemCode, status, relatedDocument, evidenceName, modifier);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error updating operational status"));
        }
    }

    @PostMapping("/save-details/{detailItemCode}/update-defectManage")
    public ResponseEntity<?> saveDefectManage(@PathVariable String detailItemCode, @RequestBody Map<String, String> defectManage) {
        detailItemCode = defectManage.get("detailItemCode");
        String certificationType = defectManage.get("certificationType");
        String defectContent = defectManage.get("defectContent");
        String modifier = defectManage.get("modifier");

        System.out.println("Received certificationType: " + certificationType);
        System.out.println("Received defectContent: " + defectContent);
        System.out.println("Received modifier: " + modifier);
        System.out.println("Received detailItemCode: " + detailItemCode);

        boolean success = defectManageService.updateDefectManage(detailItemCode, certificationType, defectContent, modifier);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error updating defect manage"));
        }
    }

    private final String bucketName = "scms-1862c.appspot.com";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();


    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("detailItemCode") String detailItemCode,
                                   RedirectAttributes redirectAttributes) {

        try {
            String fileName = evidenceDataService.uploadFile(file, detailItemCode);
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully: " + fileName);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload file.");
        }

        // 리다이렉트 URL 결정
        String redirectUrl;
        if (detailItemCode.startsWith("MNG")) {
            redirectUrl = "/manage-system/" + detailItemCode;
        } else if (detailItemCode.startsWith("PRT")) {
            redirectUrl = "/protect-measures/" + detailItemCode;
        } else if (detailItemCode.startsWith("PRC")) {
            redirectUrl = "/privacy-require/" + detailItemCode;
        } else {
            redirectUrl = "/"; // 기본 리다이렉트 URL
        }

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/files/{detailItemCode}")
    @ResponseBody
    public List<Map<String, Object>> getFiles(@PathVariable String detailItemCode) {
        return evidenceDataService.listFilesInFolder(detailItemCode);
    }

    @GetMapping("/modal-files/{detailItemCode}")
    @ResponseBody
    public List<EvidenceData> getFilesByDetailItemCode(@PathVariable String detailItemCode) {
        return evidenceDataService.getEvidenceDataByDCode(detailItemCode);
    }

    @GetMapping("/evidence-modification-info/{detailItemCode}")
    @ResponseBody
    public Map<String, Object> getModificationInfo(@PathVariable String detailItemCode) {
        return evidenceDataService.getLatestModificationInfo(detailItemCode);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName,
                                                 @RequestParam("detailItemCode") String detailItemCode,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        try {
            String folderPath = detailItemCode + "/" + fileName;
            Blob blob = storage.get(BlobId.of(bucketName, folderPath));

            if (blob == null) {
                return ResponseEntity.notFound().build();
            }

            // 파일 다운로드 로그 저장
            AccessLog accessLog = new AccessLog();
            accessLog.setAccessId(user.getNickname());  // 접속자 ID 설정
            accessLog.setAction("DOWNLOAD");  // ACT를 "DOWNLOAD"로 설정
            accessLog.setAccessPath("/" + detailItemCode + "/" + fileName);  // PATH를 설정
            accessLog.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));  // 현재 시간 설정
            accessLogService.saveAccessLog(accessLog);  // 로그 저장

            ByteArrayResource resource = new ByteArrayResource(blob.getContent());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(blob.getSize())
                    .contentType(MediaType.parseMediaType(blob.getContentType()))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/delete-file")
    @ResponseBody
    public ResponseEntity<Void> deleteFile(@RequestBody Map<String, String> payload) {
        String fileName = payload.get("fileName");
        String detailItemCode = payload.get("detailItemCode");

        try {
            evidenceDataService.deleteFile(detailItemCode, fileName);

            // Debugging statements
            System.out.println("mdic: " + detailItemCode);
            System.out.println("mfn: " + fileName);

            return ResponseEntity.ok().build(); // 성공 시 200 OK 반환
        } catch (Exception e) {
            e.printStackTrace(); // 로그에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 실패 시 500 에러 반환
        }
    }


//    @DeleteMapping("/delete")
//    @ResponseBody
//    public ResponseEntity<?> deleteFile(@PathVariable String detailItemCode, @PathVariable String fileName) {
//        //String detailItemCode = (String) request.get("detailItemCode");
//        //String fileName = (String) request.get("fileName");
//
//        boolean success = evidenceDataService.deleteFile(detailItemCode, fileName);
//
//        // Debugging statements
//        System.out.println("mdic: " + detailItemCode);
//        System.out.println("mfn: " + fileName);
//
//        if (success) {
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}

//    @GetMapping("/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName,
//                                                 @RequestParam("detailItemCode") String detailItemCode) {
//        try {
//            String folderPath = detailItemCode + "/" + fileName;
//            Blob blob = storage.get(BlobId.of(bucketName, folderPath));
//
//            if (blob == null) {
//                return ResponseEntity.notFound().build();
//            }
//
//            ByteArrayResource resource = new ByteArrayResource(blob.getContent());
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentLength(blob.getSize())
//                    .contentType(MediaType.parseMediaType(blob.getContentType()))
//                    .body(resource);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

//    @PostMapping("/delete/{detailItemCode}/{fileName}")
//    public ResponseEntity<Void> deleteFile(@RequestBody Map<String, Object> request, @PathVariable String detailItemCode, @PathVariable String fileName) {
//        //String detailItemCode = (String) request.get("detailItemCode");
//        //String fileName = (String) request.get("fileName");
//        //double fileSize = (Double) request.get("fileSize");
//
//        evidenceDataService.deleteFile(detailItemCode, fileName);
//
//        // Debugging statements
//        System.out.println("mdic: " + detailItemCode);
//        System.out.println("mfn: " + fileName);
//        //System.out.println("mfs: " + fileSize);
//
//        return ResponseEntity.ok().build();
//    }



//    @DeleteMapping("/delete-file")
//    @ResponseBody
//    public ResponseEntity<?> deleteFile(@RequestParam String fileName, @RequestParam String detailItemCode) {
//        try {
//            evidenceDataService.deleteFile(fileName, detailItemCode);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }






