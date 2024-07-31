package com.yeogi.scms.service;

import com.yeogi.scms.domain.DefectManage;
import com.yeogi.scms.repository.DefectManageRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
