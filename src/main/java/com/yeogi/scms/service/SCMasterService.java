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

    public List<SCMaster> getFilteredSCMaster(String sccode) {
        List<SCMaster> allRecords = repository.findBySCCode(sccode);

        // 최신 생성일을 가진 행의 인증년도를 찾음
        Optional<SCMaster> latestRecordOpt = allRecords.stream()
                .max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

        if (latestRecordOpt.isPresent()) {
            int latestCertificationYear = latestRecordOpt.get().getCertificationYear();

            // 해당 인증년도와 같은 행만 필터링
            return allRecords.stream()
                    .filter(record -> record.getCertificationYear() == latestCertificationYear)
                    .collect(Collectors.toList());
        }

        return List.of();
    }


}
