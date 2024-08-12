package com.yeogi.scms.service;

import com.yeogi.scms.domain.CertifDetail;
import com.yeogi.scms.domain.SCMaster;
import com.yeogi.scms.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SCMasterService {

    private final SCMasterRepository repository;

    @Autowired
    public SCMasterService(SCMasterRepository repository) {
        this.repository = repository;
    }

    public List<SCMaster> getSCMasterBySCCode(String sccode) {
        List<SCMaster> allRecords = repository.findBySCCode(sccode);
        return getByLatestYear(allRecords);
    }

    public boolean saveDetailsToDB(String documentCode, String isoDetails, String pciDssDetails) {
        try {
            repository.updateDetails(documentCode, isoDetails, pciDssDetails);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<SCMaster> getByLatestYear(List<SCMaster> records) {
        Optional<SCMaster> latestRecordOpt = records.stream()
                .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

        if (latestRecordOpt.isPresent()) {
            int latestCertificationYear = latestRecordOpt.get().getCertificationYear();
            return records.stream()
                    .filter(record -> record.getCertificationYear() == latestCertificationYear)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    //가장 최근 인증년도를 반환하는 메서드
    public int getLatestCertificationYear() {
        List<SCMaster> allRecords = repository.findAll();
        Optional<SCMaster> latestRecordOpt = allRecords.stream()
                .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

        if (latestRecordOpt.isPresent()) {
            return latestRecordOpt.get().getCertificationYear();
        } else {
            throw new RuntimeException("No certification data available.");
        }
    }


}
