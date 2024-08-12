package com.yeogi.scms.controller;

import com.yeogi.scms.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DashBoardController {

    private final SCMasterService scmMasterService;
    private final DashBoardService dashBoardService;
    private final MonthlyIndexInfoService monthlyIndexInfoService;
    private static final Logger logger = LoggerFactory.getLogger(DashBoardController.class);


    @Autowired
    public DashBoardController(SCMasterService scmMasterService, DashBoardService dashBoardService, MonthlyIndexInfoService monthlyIndexInfoService) {
        this.scmMasterService = scmMasterService;
        this.dashBoardService = dashBoardService;
        this.monthlyIndexInfoService = monthlyIndexInfoService;
    }

    private void addNicknameToModel(Model model, CustomUserDetails user) {
        String nickname = (user != null) ? user.getNickname() : "Guest";
        model.addAttribute("nickname", nickname);
    }

    @GetMapping("/")
    public String showDashboard(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        addNicknameToModel(model, user);

        try {
            // 최근 인증년도 가져오기
            int latestYear = scmMasterService.getLatestCertificationYear();

            // 인증년도에 따른 데이터를 가져와서 모델에 추가
            model.addAttribute("latestYear", latestYear);
            model.addAttribute("manageCompleted", dashBoardService.getCertifDetailBySCCode("MANAGE", latestYear));
            model.addAttribute("privacyCompleted", dashBoardService.getCertifDetailBySCCode("PRIVACY", latestYear));
            model.addAttribute("protectCompleted", dashBoardService.getCertifDetailBySCCode("PROTECT", latestYear));
            model.addAttribute("monthlyIndexInfo", monthlyIndexInfoService.getMonthlyIndexInfoByYear(latestYear));
            model.addAttribute("monthlyCompletedBySCCode", dashBoardService.getMonthlyCompletedBySCCode(latestYear));

            return "main"; // 대시보드 템플릿을 렌더링

        } catch (Exception e) {
            logger.error("Error loading dashboard for user: " + user.getUsername(), e);
            model.addAttribute("error", "An error occurred while loading the dashboard.");
            return "error";
        }
    }

    @GetMapping("/updateGraphs")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateGraphs(@RequestParam("year") int year) {
        try {
            Map<String, Object> response = Map.of(
                    "manageCompleted", dashBoardService.getCertifDetailBySCCode("MANAGE", year),
                    "privacyCompleted", dashBoardService.getCertifDetailBySCCode("PRIVACY", year),
                    "protectCompleted", dashBoardService.getCertifDetailBySCCode("PROTECT", year),
                    "monthlyIndexInfo", monthlyIndexInfoService.getMonthlyIndexInfoByYear(year),
                    "monthlyCompletedBySCCode", dashBoardService.getMonthlyCompletedBySCCode(year)
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating graphs for year: " + year, e);
            return ResponseEntity.status(500).body(Map.of("error", "Error fetching data: " + e.getMessage()));
        }
    }
}