package com.yeogi.scms.service;

import com.yeogi.scms.domain.CertifDetail;
import com.yeogi.scms.repository.CertifDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashBoardService {

    private final CertifDetailRepository certifDetailRepository;

    @Autowired
    public DashBoardService(CertifDetailRepository certifDetailRepository) {
        this.certifDetailRepository = certifDetailRepository;
    }

    // 특정 년도에 해당하는 CertifDetail 데이터 필터링
    public List<CertifDetail> getFilteredCertifDetail(int certificationYear) {
        return certifDetailRepository.findByCertificationYear(certificationYear);
    }

    // Classification_Code별로 데이터를 그룹화하여 통계 생성 (연도 필터링 적용)
    public Map<String, Object> getCertifDetailBySCCode(String scCode, int certificationYear) {
        List<CertifDetail> filteredDetails = getFilteredCertifDetail(certificationYear);
        List<CertifDetail> detailsByScCode = filteredDetails.stream()
                .filter(detail -> scCode.equals(detail.getClassificationCode()))
                .collect(Collectors.toList());

        long completedCount = detailsByScCode.stream().filter(CertifDetail::isCompleted).count();
        long totalCount = detailsByScCode.size();
        double percentage = (totalCount > 0) ? (completedCount * 100.0 / totalCount) : 0.0;

        Map<String, Object> result = new HashMap<>();
        result.put("completedCount", completedCount);
        result.put("totalCount", totalCount);
        result.put("percentage", String.format("%.2f", percentage));
        return result;
    }

    // Classification_Code별로 monthly_index에 따른 completed 개수를 누적하여 반환하는 메서드
    public Map<String, Map<Integer, Long>> getMonthlyCompletedBySCCode(int year) {
        List<CertifDetail> filteredDetails = getFilteredCertifDetail(year);

        // 결과를 저장할 Map 생성
        Map<String, Map<Integer, Long>> result = new HashMap<>();

        // Classification_Code: MANAGE, PRIVACY, PROTECT
        String[] codes = {"MANAGE", "PRIVACY", "PROTECT"};

        for (String scCode : codes) {
            // 기본적인 monthly_index별 completed 개수를 계산
            Map<Integer, Long> monthlyCompleted = new HashMap<>();

            // 누적합 계산
            long cumulativeSum = 0;
            for (int i = 1; i <= 12; i++) {
                final int currentIndex = i;  // i 값을 람다식에서 사용할 수 있도록 final 변수로 저장
                long currentCount = filteredDetails.stream()
                        .filter(detail -> scCode.equals(detail.getClassificationCode()))  // Classification_Code 필터링
                        .filter(detail -> detail.getMonthlyIndex() == currentIndex)  // 현재 index에 해당하는 데이터만 필터링
                        .filter(CertifDetail::isCompleted)  // 완료된 항목만 필터링
                        .count();  // 완료된 항목의 개수를 계산

                cumulativeSum += currentCount;
                monthlyCompleted.put(currentIndex, cumulativeSum);
            }


            result.put(scCode, monthlyCompleted);
        }

        return result;
    }

}


