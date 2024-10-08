package com.yeogi.scms.service;

import com.yeogi.scms.domain.DefectManage;
import com.yeogi.scms.repository.DefectManageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefectManageService {

    private final DefectManageRepository defectManageRepository;

    @Autowired
    public DefectManageService(DefectManageRepository defectManageRepository) {
        this.defectManageRepository = defectManageRepository;
    }

    public List<DefectManage> getDefectManageByDCode(String detailItemCode) {
        return defectManageRepository.findByDetailCode(detailItemCode);
    }

    public boolean updateDefectManage(String detailItemCode, String certificationType, String defectContent, String modifier) {
        try {
            // 현재 로그인한 사용자의 닉네임을 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            modifier  = userDetails.getNickname();

            defectManageRepository.updateDefectManage(detailItemCode, certificationType, defectContent, modifier);

            System.out.println("Certification Type: " + certificationType);
            System.out.println("Defect Content: " + defectContent);
            System.out.println("Modifier: " + modifier);
            System.out.println("Detail Item Code: " + detailItemCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
