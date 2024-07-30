package com.yeogi.scms.controller;

import com.yeogi.scms.domain.CertifDetail;
import com.yeogi.scms.domain.SCMaster;
import com.yeogi.scms.service.CertifDetailService;
import com.yeogi.scms.service.SCMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final SCMasterService scmMasterService;
    private final CertifDetailService certifDetailService;

    @Autowired
    public MainController(SCMasterService scmMasterService, CertifDetailService certifDetailService) {
        this.scmMasterService = scmMasterService;
        this.certifDetailService = certifDetailService;
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
        CertifDetail detail = certifDetailService.getCertifDetailByDCode(detailItemCode);
        model.addAttribute("detail", detail);
        return "detail-template";
    }

    @GetMapping("/log-manage")
    public String showLogManagement() {
        return "logManage";
    }


}