package com.yeogi.scms.service;

import com.yeogi.scms.domain.CertifContent;
import com.yeogi.scms.repository.CertifContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean saveCertifContentToDB(String detailItemCode, String certificationCriteria, String keyCheckpoints, String relevantLaws){
        try {
            certifContentRepository.updateCertifContent(detailItemCode, certificationCriteria, keyCheckpoints, relevantLaws);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
