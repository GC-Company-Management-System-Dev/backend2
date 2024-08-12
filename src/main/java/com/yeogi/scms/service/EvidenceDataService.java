package com.yeogi.scms.service;

import com.yeogi.scms.domain.EvidenceData;
import com.yeogi.scms.repository.EvidenceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvidenceDataService {

    private final EvidenceDataRepository evidenceDataRepository;

    @Autowired
    public EvidenceDataService(EvidenceDataRepository evidenceDataRepository) {
        this.evidenceDataRepository = evidenceDataRepository;
    }

    public List<EvidenceData> getEvidenceDataByDCode(String detailItemCode) {
        return evidenceDataRepository.findByDetailCode(detailItemCode);
    }

    public void saveEvidenceData(EvidenceData evidenceData) {
        evidenceDataRepository.save(evidenceData);
    }
}
