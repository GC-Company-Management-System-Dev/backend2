package com.yeogi.scms.controller;

import com.yeogi.scms.domain.*;
import com.yeogi.scms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    //private final EvidenceDataService evidenceDataService;

    @Autowired
    public MainController(SCMasterService scmMasterService, CertifDetailService certifDetailService, CertifContentService certifContentService,
                          DefectManageService defectManageService,
                          OperationalStatusService operationalStatusService, EvidenceDataService evidenceDataService) {
        this.scmMasterService = scmMasterService;
        this.certifDetailService = certifDetailService;
        this.certifContentService = certifContentService;
        this.defectManageService = defectManageService;
        this.operationalStatusService = operationalStatusService;
        //this.evidenceDataService = evidenceDataService;

    }

    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/manage-system")
    public String showManageSystem(Model model) {
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
    public String showProtectMeasures(Model model) {
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
    public String showPrivacyRequire(Model model) {
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
    public String showDetail(@PathVariable String detailItemCode, Model model) {
        List<CertifDetail> details = certifDetailService.getCertifDetailByDCode(detailItemCode);
        List<CertifContent> certifContents = certifContentService.getCertifContentByDCode(detailItemCode);
        List<DefectManage> defectManages = defectManageService.getDefectManageByDCode(detailItemCode);
        List<OperationalStatus> operationalStatuses = operationalStatusService.getOperationalStatusByDCode(detailItemCode);
        //List<EvidenceData> evidenceDataList = evidenceDataService.getEvidenceDataByDCode(detailItemCode);

        // detailItemCode와 일치하는 행을 찾음
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
    public String showLogManagement() {
        return "logManage";
    }

    @GetMapping("/login")
    public String showLogin(){
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin() {
        // 로그인 처리 로직을 여기에 추가할 수 있습니다.
        return "redirect:/";  // 로그인 성공 시 메인 페이지로 리디렉션
    }


}