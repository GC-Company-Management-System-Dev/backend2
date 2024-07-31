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
}
