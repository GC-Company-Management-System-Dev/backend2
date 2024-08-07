package com.yeogi.scms.service;

import com.yeogi.scms.domain.CertifDetail;
import com.yeogi.scms.domain.MonthlyIndexInfo;
import com.yeogi.scms.repository.CertifDetailRepository;
import com.yeogi.scms.repository.MonthlyIndexInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CertifDetailService {
    private CertifDetailRepository certifDetailRepository;
    private MonthlyIndexInfoRepository monthlyIndexInfoRepository;

    @Autowired
    public CertifDetailService(CertifDetailRepository certifDetailRepository, MonthlyIndexInfoRepository monthlyIndexInfoRepository) {
        this.certifDetailRepository = certifDetailRepository;
        this.monthlyIndexInfoRepository = monthlyIndexInfoRepository;
    }

    public List<CertifDetail> getCertifDetailByDoccode(String documentCode) {
        return certifDetailRepository.findByDocCode(documentCode);
    }

    public List<CertifDetail> getCertifDetailByDCode(String detailItemCode) {
        return certifDetailRepository.findByDetailCode(detailItemCode);
    }

    // 작성 완료 체크 박스
    public boolean updateCompletionStatus(String detailItemCode, boolean completed) {
        int rowsUpdated = certifDetailRepository.updateCompletionStatus(detailItemCode, completed);

        if (completed) {
            // If completed, update the Monthly_Index
            return rowsUpdated > 0 && updateMonthlyIndex(detailItemCode);
        } else {
            // If not completed, delete the Monthly_Index
            int rowsDeleted = certifDetailRepository.deleteMonthlyIndex(detailItemCode);
            return rowsUpdated > 0 && rowsDeleted > 0;
        }
    }

    public boolean updateMonthlyIndex(String detailItemCode) {
        // detailItemCode에 맵핑되는 행의 인증년도를 가져옴
        Integer certificationYear = certifDetailRepository.findCertificationYearByDetailItemCode(detailItemCode);
        if (certificationYear == null) {
            return false;
        }

        // 그 인증년도로 IndexInfo 테이블의 행을 가져옴
        MonthlyIndexInfo monthlyIndexInfo = monthlyIndexInfoRepository.findByCertificationYear(certificationYear);
        if (monthlyIndexInfo == null) {
            return false;
        }

        // 지금 시점에 몇월인지 가져오고 인덱싱 값을 결정함
        int month = LocalDate.now().getMonthValue();
        Integer monthlyValue = getMonthlyValue(monthlyIndexInfo, month);

        // Certification_Detail_Item table에 해당 값을 인서트함
        if (monthlyValue != null) {
            certifDetailRepository.updateMonthlyIndex(detailItemCode, monthlyValue);
            return true;
        } else {
            return false;
        }
    }

    private Integer getMonthlyValue(MonthlyIndexInfo monthlyIndexInfo, int month) {
        switch (month) {
            case 1:
                return monthlyIndexInfo.getJan();
            case 2:
                return monthlyIndexInfo.getFeb();
            case 3:
                return monthlyIndexInfo.getMar();
            case 4:
                return monthlyIndexInfo.getApr();
            case 5:
                return monthlyIndexInfo.getMay();
            case 6:
                return monthlyIndexInfo.getJun();
            case 7:
                return monthlyIndexInfo.getJul();
            case 8:
                return monthlyIndexInfo.getAug();
            case 9:
                return monthlyIndexInfo.getSep();
            case 10:
                return monthlyIndexInfo.getOct();
            case 11:
                return monthlyIndexInfo.getNov();
            case 12:
                return monthlyIndexInfo.getDecem();
            default:
                return null;
        }
    }
}

