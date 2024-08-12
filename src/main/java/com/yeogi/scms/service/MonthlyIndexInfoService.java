package com.yeogi.scms.service;

import com.yeogi.scms.domain.MonthlyIndexInfo;
import com.yeogi.scms.repository.MonthlyIndexInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonthlyIndexInfoService {

    private final MonthlyIndexInfoRepository monthlyIndexInfoRepository;

    @Autowired
    public MonthlyIndexInfoService(MonthlyIndexInfoRepository monthlyIndexInfoRepository) {
        this.monthlyIndexInfoRepository = monthlyIndexInfoRepository;
    }

    // 특정 인증 년도의 MonthlyIndexInfo 데이터를 가져오는 메서드
    public MonthlyIndexInfo getMonthlyIndexInfoByYear(int certificationYear) {
        List<MonthlyIndexInfo> monthlyIndexInfos = monthlyIndexInfoRepository.findByCertificationYear(certificationYear);

        // 특정 연도에 대해 하나의 MonthlyIndexInfo만 존재
        return monthlyIndexInfos.isEmpty() ? null : monthlyIndexInfos.get(0);
    }

    // 최신 인증 년도의 MonthlyIndexInfo 데이터를 가져오는 메서드
    public MonthlyIndexInfo getLatestMonthlyIndexInfo() {
        List<MonthlyIndexInfo> monthlyIndexInfos = monthlyIndexInfoRepository.findLatest();

        // 최신 인증 연도의 데이터는 하나만 반환됨
        return monthlyIndexInfos.isEmpty() ? null : monthlyIndexInfos.get(0);
    }
}
