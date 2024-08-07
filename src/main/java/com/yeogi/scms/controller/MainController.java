package com.yeogi.scms.controller;

import com.yeogi.scms.domain.*;
import com.yeogi.scms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Controller
public class MainController {

    private final CertifContentService certifContentService;
    private final DefectManageService defectManageService;
    private final OperationalStatusService operationalStatusService;
    private final SCMasterService scmMasterService;
    private final CertifDetailService certifDetailService;
    private final LoginAccountService loginAccountService;
    private final AccessLogService accessLogService;

    private EvidenceDataService evidenceDataService;

    private final AuthenticationService authenticationService;
    private final Path root = Paths.get("uploads");

    @Autowired
    public MainController(SCMasterService scmMasterService, CertifDetailService certifDetailService, CertifContentService certifContentService,
                          DefectManageService defectManageService, OperationalStatusService operationalStatusService,
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

    @GetMapping("/")
    public String main(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);
        return "main";
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


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("detailItemCode") String detailItemCode,
                                             @RequestParam("creator") String creator) {
        try {
            UUID fileKey = UUID.randomUUID();
            String fileName = file.getOriginalFilename();
            double fileSize = file.getSize();
            Path filePath = this.root.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            EvidenceData evidenceData = new EvidenceData();
            evidenceData.setFileKey(fileKey);
            evidenceData.setDetailItemCode(detailItemCode);
            evidenceData.setFileName(fileName);
            evidenceData.setFileSize(fileSize);
            evidenceData.setFilePath(filePath.toString());
            evidenceData.setCreator(creator);
            //evidenceDataService.saveEvidenceData(evidenceData);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    @GetMapping("/files/{detailItemCode}")
    public List<EvidenceData> getFiles(@PathVariable String detailItemCode) {
        return evidenceDataService.getEvidenceDataByDCode(detailItemCode);
    }


}

