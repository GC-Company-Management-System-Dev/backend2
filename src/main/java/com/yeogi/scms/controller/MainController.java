package com.yeogi.scms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/management-system")
    public String showManagementSystem() {
        return "managementSystem";
    }

    @GetMapping("/security-measures")
    public String showSecurityMeasures() {
        return "securityMeasures";
    }

    @GetMapping("/privacy-requirements")
    public String showPrivacyRequirements() {
        return "privacyRequirements";
    }

    @GetMapping("/log-management")
    public String showLogManagement() {
        return "logManagement";
    }

    @GetMapping("/detail-mvc")
    public String showDetailTemplate() { return "detail-template";}
}
