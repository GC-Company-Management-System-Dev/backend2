package com.yeogi.scms.service;

import com.yeogi.scms.domain.CertifDetail;
import com.yeogi.scms.repository.CertifDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertifDetailService {

    private final CertifDetailRepository certifDetailRepository;

    @Autowired
    public CertifDetailService(CertifDetailRepository certifDetailRepository) {
        this.certifDetailRepository = certifDetailRepository;
    }

    public List<CertifDetail> getFilteredCertifDetail(String documentCode) {
        return certifDetailRepository.findByDocuCode(documentCode);
    }

    public List<CertifDetail> getCertifDetailByDCode(String detailItemCode) {
        return certifDetailRepository.findByDetailCode(detailItemCode);
    }

    // 작성 완료 체크 박스
    public boolean updateCompletionStatus(String detailItemCode, boolean completed) {
        int rowsUpdated = certifDetailRepository.updateCompletionStatus(detailItemCode, completed);
        return rowsUpdated > 0;
    }

}
