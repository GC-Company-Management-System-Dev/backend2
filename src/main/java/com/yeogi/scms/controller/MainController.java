package com.yeogi.scms.controller;

import com.yeogi.scms.domain.*;
import com.yeogi.scms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final CertifContentService certifContentService;
    private final DefectManageService defectManageService;
    private final OperationalStatusService operationalStatusService;
    private final SCMasterService scmMasterService;
    private final CertifDetailService certifDetailService;

    private final LoginAccountService loginAccountService;

    @Autowired
    public MainController(SCMasterService scmMasterService, CertifDetailService certifDetailService, CertifContentService certifContentService,
                          DefectManageService defectManageService, OperationalStatusService operationalStatusService,
                          LoginAccountService loginAccountService) {
        this.scmMasterService = scmMasterService;
        this.certifDetailService = certifDetailService;
        this.certifContentService = certifContentService;
        this.defectManageService = defectManageService;
        this.operationalStatusService = operationalStatusService;
        this.loginAccountService = loginAccountService;

    }

    private void addNicknameToModel(Model model, User user) {
        String nickname = (user != null) ? loginAccountService.getNicknameByUsername(user.getUsername()) : "Guest";
        model.addAttribute("nickname", nickname);
    }

    @GetMapping("/")
    public String main(Model model, @AuthenticationPrincipal User user) {
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
    public String showManageSystem(Model model, @AuthenticationPrincipal User user) {
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
    public String showProtectMeasures(Model model, @AuthenticationPrincipal User user) {
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
    public String showPrivacyRequire(Model model, @AuthenticationPrincipal User user) {
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
    public String showDetail(@PathVariable String detailItemCode, Model model, @AuthenticationPrincipal User user) {
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
    public String showLogManagement(Model model, @AuthenticationPrincipal User user) {
        addNicknameToModel(model, user);
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

    @PostMapping("/save-certifContent")
    public ResponseEntity<?> saveCertifContent(@RequestBody Map<String, String> certifContent) {
        String detailItemCode = certifContent.get("detailItemCode");
        String certificationCriteria = certifContent.get("certificationCriteria");
        String keyCheckpoints = certifContent.get("keyCheckpoints");
        String relevantLaws = certifContent.get("relevantLaws");

        boolean success = certifContentService.saveCertifContentToDB(detailItemCode, certificationCriteria, keyCheckpoints, relevantLaws);

        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error saving details"));
        }
    }




}