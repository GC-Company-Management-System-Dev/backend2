package com.yeogi.scms.service;

import com.yeogi.scms.domain.CertifContent;
import com.yeogi.scms.repository.CertifContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class CertifContentService {

    private final CertifContentRepository certifContentRepository;

    @Autowired
    public CertifContentService(CertifContentRepository certifContentRepository) {
        this.certifContentRepository = certifContentRepository;
    }

    public List<CertifContent> getCertifContentByDCode(String detailItemCode) {
        return certifContentRepository.findByDetailCode(detailItemCode);
    }

    public boolean updateCertifContent(String detailItemCode, String certificationCriteria, String keyCheckpoints, String relevantLaws, String modifier){
        try {
            // 현재 로그인한 사용자의 닉네임을 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            modifier = authentication.getName(); // UserDetails의 getUsername()을 통해 가져온다

            certifContentRepository.updateCertifContent(detailItemCode, certificationCriteria, keyCheckpoints, relevantLaws, modifier);
            // Debugging statements
            System.out.println("Certification Criteria: " + certificationCriteria);
            System.out.println("Key Checkpoints: " + keyCheckpoints);
            System.out.println("Relevant Laws: " + relevantLaws);
            System.out.println("Modifier: " + modifier);
            System.out.println("Detail Item Code: " + detailItemCode);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
