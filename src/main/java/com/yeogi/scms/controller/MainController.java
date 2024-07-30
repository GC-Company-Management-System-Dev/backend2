package com.yeogi.scms.controller;

import com.yeogi.scms.service.SCMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final SCMasterService SCMasterService;

    @Autowired
    public MainController(SCMasterService SCMasterService) {
        this.SCMasterService = SCMasterService;
    }

    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/manage-system")
    public String showManagementSystem(Model model) {
        model.addAttribute("codes", SCMasterService.getFilteredSCMaster("MANAGE"));
        return "manageSystem";
    }

    @GetMapping("/protect-measures")
    public String showProtectMeasures(Model model) {
        model.addAttribute("codes", SCMasterService.getFilteredSCMaster("PROTECT"));
        return "protectMeasures";
    }

    @GetMapping("/privacy-require")
    public String showPrivacyRequire(Model model) {
        model.addAttribute("codes", SCMasterService.getFilteredSCMaster("PRIVACY"));
        return "privacyRequire";
    }

    @GetMapping("/log-manage")
    public String showLogManagement() {
        return "logManage";
    }

    @GetMapping("/detail-mvc")
    public String showDetailTemplate() {
        return "detail-template";
    }
}
