package com.yeogi.scms.service;

import com.yeogi.scms.domain.OperationalStatus;
import com.yeogi.scms.repository.OperationalStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public boolean updateOperationalStatus(String detailItemCode, String status, String relatedDocument, String evidenceName, String modifier){
        try {
            // 현재 로그인한 사용자의 닉네임을 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            modifier  = userDetails.getNickname();

            operationalStatusRepository.updateOperationalStatus(detailItemCode, status, relatedDocument, evidenceName, modifier);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
