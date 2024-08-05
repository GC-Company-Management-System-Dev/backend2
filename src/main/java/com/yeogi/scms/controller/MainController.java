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

@Controller
public class MainController {

    private final CertifContentService certifContentService;
    private final DefectManageService defectManageService;
    private final OperationalStatusService operationalStatusService;
    private final SCMasterService scmMasterService;
    private final CertifDetailService certifDetailService;
    private final LoginAccountService loginAccountService;
    private final AccessLogService accessLogService;

    @Autowired
    public MainController(SCMasterService scmMasterService, CertifDetailService certifDetailService, CertifContentService certifContentService,
                          DefectManageService defectManageService, OperationalStatusService operationalStatusService,
                          LoginAccountService loginAccountService, AccessLogService accessLogService) {
        this.scmMasterService = scmMasterService;
        this.certifDetailService = certifDetailService;
        this.certifContentService = certifContentService;
        this.defectManageService = defectManageService;
        this.operationalStatusService = operationalStatusService;
        this.loginAccountService = loginAccountService;
        this.accessLogService = accessLogService;

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

    @GetMapping("/manage-system")
    public String showManageSystem(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);
        // (1) SCMasterService.getFilteredSCMaster("MANAGE") 호출
        List<SCMaster> scmMasters = scmMasterService.getFilteredSCMaster("MANAGE");

        // (2) 각 SCMaster의 documentCode로 CertifDetailService.getFilteredCertifDetail(documentCode) 호출
        Map<String, List<CertifDetail>> certifDetailsMap = scmMasters.stream()
                .collect(Collectors.toMap(
                        SCMaster::getDocumentCode,
                        master -> certifDetailService.getFilteredCertifDetail(master.getDocumentCode())
                ));

        // 모델에 추가
        model.addAttribute("scmMasters", scmMasters);
        model.addAttribute("certifDetails", certifDetailsMap);

        return "manageSystem";
    }

    @GetMapping("/protect-measures")
    public String showProtectMeasures(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);

        List<SCMaster> scmMasters = scmMasterService.getFilteredSCMaster("PROTECT");

        Map<String, List<CertifDetail>> certifDetailsMap = scmMasters.stream()
                .collect(Collectors.toMap(
                        SCMaster::getDocumentCode,
                        master -> certifDetailService.getFilteredCertifDetail(master.getDocumentCode())
                ));

        model.addAttribute("scmMasters", scmMasters);
        model.addAttribute("certifDetails", certifDetailsMap);

        return "protectMeasures";
    }

    @GetMapping("/privacy-require")
    public String showPrivacyRequire(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);
        List<SCMaster> scmMasters = scmMasterService.getFilteredSCMaster("PRIVACY");

        Map<String, List<CertifDetail>> certifDetailsMap = scmMasters.stream()
                .collect(Collectors.toMap(
                        SCMaster::getDocumentCode,
                        master -> certifDetailService.getFilteredCertifDetail(master.getDocumentCode())
                ));

        model.addAttribute("scmMasters", scmMasters);
        model.addAttribute("certifDetails", certifDetailsMap);

        return "privacyRequire";
    }

    @GetMapping("/{detailItemCode}")
    public String showDetail(@PathVariable String detailItemCode, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);

        List<CertifDetail> details = certifDetailService.getCertifDetailByDCode(detailItemCode);
        List<CertifContent> certifContents = certifContentService.getCertifContentByDCode(detailItemCode);
        List<DefectManage> defectManages = defectManageService.getDefectManageByDCode(detailItemCode);
        List<OperationalStatus> operationalStatuses = operationalStatusService.getOperationalStatusByDCode(detailItemCode);
        //List<EvidenceData> evidenceDataList = evidenceDataService.getEvidenceDataByDCode(detailItemCode);

        // detailItemCode와 일치하는 행을 찾아 detailItemTypeName 저장
        String detailItemTypeName = details.stream()
                .filter(detail -> detailItemCode.equals(detail.getDetailItemCode()))
                .map(CertifDetail::getDetailItemTypeName)
                .findFirst()
                .orElse("");

        model.addAttribute("detailItemTypeName", detailItemTypeName);
        model.addAttribute("certifContents", certifContents);
        model.addAttribute("defectManages", defectManages);
        model.addAttribute("operationalStatuses", operationalStatuses);
        //model.addAttribute("evidenceDataList", evidenceDataList);

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


    @PostMapping("/save-details")
    public ResponseEntity<?> saveDetails(@RequestBody Map<String, String> details) {
        String documentCode = details.get("documentCode");
        String isoDetails = details.get("isoDetails");
        String pciDssDetails = details.get("pciDssDetails");

        boolean success = scmMasterService.saveDetailsToDB(documentCode, isoDetails, pciDssDetails);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error saving details"));
        }
    }

    @PostMapping("/update-certifContent")
    public ResponseEntity<?> saveCertifContent(@RequestBody Map<String, String> certifContent) {
        String detailItemCode = certifContent.get("detailItemCode");
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

    @PostMapping("/update-operationalStatus")
    public ResponseEntity<?> saveOperationalStatus(@RequestBody Map<String, String> operationalStatus) {
        String detailItemCode = operationalStatus.get("detailItemCode");
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

    @PostMapping("/update-defectManage")
    public ResponseEntity<?> saveDefectManage(@RequestBody Map<String, String> defectManage) {
        String detailItemCode = defectManage.get("detailItemCode");
        String ismsP = defectManage.get("ismsP");
        String iso27k = defectManage.get("iso27k");
        String pciDss = defectManage.get("pciDss");
        String modifier = defectManage.get("modifier");

        boolean success = defectManageService.updateDefectManage(detailItemCode, ismsP, iso27k, pciDss, modifier);

        // Debugging statements
        System.out.println("Certification Criteria: " + ismsP);
        System.out.println("Key Checkpoints: " + iso27k);
        System.out.println("Relevant Laws: " + pciDss);
        System.out.println("Modifier: " + modifier);
        System.out.println("Detail Item Code: " + detailItemCode);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error updating defect manage"));
        }
    }

}
