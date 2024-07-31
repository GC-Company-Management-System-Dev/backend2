package com.yeogi.scms.service;

import com.yeogi.scms.domain.OperationalStatus;
import com.yeogi.scms.repository.OperationalStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationalStatusService {

    private final OperationalStatusRepository operationalStatusRepository;

    @Autowired
    public OperationalStatusService(OperationalStatusRepository operationalStatusRepository) {
        this.operationalStatusRepository = operationalStatusRepository;
    }

    public List<OperationalStatus> getOperationalStatusByDCode(String detailItemCode) {
        return operationalStatusRepository.findByDetailCode(detailItemCode);
    }
}
