package com.yeogi.scms.service;

import com.yeogi.scms.domain.*;
import com.yeogi.scms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.util.*;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class DataInitializationService {

    private final SCMasterRepository scMasterRepository;
    private final CertifDetailRepository certifDetailRepository;
    private final CertifDetailService certifDetailService;
    private final CertifContentRepository certifContentRepository;
    private final DefectManageRepository defectManageRepository;
    private final OperationalStatusRepository operationalStatusRepository;
    private final EvidenceDataRepository evidenceDataRepository;
    private final MonthlyIndexInfoRepository monthlyIndexInfoRepository;
    private final String bucketName = "scms-1862c.appspot.com";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Autowired
    public DataInitializationService(
            SCMasterRepository scMasterRepository,
            CertifDetailRepository certifDetailRepository,
            CertifDetailService certifDetailService,
            CertifContentRepository certifContentRepository,
            DefectManageRepository defectManageRepository,
            OperationalStatusRepository operationalStatusRepository,
            EvidenceDataRepository evidenceDataRepository,
            MonthlyIndexInfoRepository monthlyIndexInfoRepository) {
        this.scMasterRepository = scMasterRepository;
        this.certifDetailRepository = certifDetailRepository;
        this.certifDetailService = certifDetailService;
        this.certifContentRepository = certifContentRepository;
        this.defectManageRepository = defectManageRepository;
        this.operationalStatusRepository = operationalStatusRepository;
        this.evidenceDataRepository = evidenceDataRepository;
        this.monthlyIndexInfoRepository = monthlyIndexInfoRepository;
    }

    @Transactional
    public boolean initializeDataForNewYear() {
        try {
            // Step 1: SCMaster 테이블의 모든 행을 가져와 최신 인증년도 행만 필터링
            List<SCMaster> filteredMasters = getFilteredSCMasters();

            // Step 2: 필터링된 CertifDetail 리스트 가져오기
            Map<String, List<CertifDetail>> certifDetailsMap = filteredMasters.stream()
                    .collect(Collectors.toMap(
                            SCMaster::getDocumentCode,
                            master -> certifDetailService.getCertifDetailByDoccode(master.getDocumentCode())
                    ));

            // step 3: SCMaster 수정 및 저장 (루프 밖에서 한 번만 저장)
            List<SCMaster> newMasters = filteredMasters.stream()
                    .map(this::createNewMasterForNextYear)
                    .collect(Collectors.toList());
            scMasterRepository.saveAll(newMasters);

            // Step 4: CertifContent, DefectManage, OperationalStatus, EvidenceData 리스트 가져오기 및 저장
            for (List<CertifDetail> details : certifDetailsMap.values()) {

                // Step 4.1: CertifDetail 수정 및 저장
                List<CertifDetail> newDetails = details.stream()
                        .map(this::createNewDetailForNextYear)
                        .collect(Collectors.toList());
                certifDetailRepository.saveAll(newDetails);

                for (CertifDetail detail : details) {

                    List<CertifContent> certifContents = certifContentRepository.findByDetailCode(detail.getDetailItemCode());
                    List<DefectManage> defectManages = defectManageRepository.findByDetailCode(detail.getDetailItemCode());
                    List<OperationalStatus> operationalStatuses = operationalStatusRepository.findByDetailCode(detail.getDetailItemCode());
                    List<EvidenceData> evidenceData = evidenceDataRepository.findByDetailCode(detail.getDetailItemCode());

                    // Step 4.2: 데이터 수정 및 테이블 갱신
                    modifyAndSaveDetails(certifContents, defectManages, operationalStatuses, evidenceData);
                }
            }

            // Step 5: MonthlyIndexInfo 테이블 갱신
            updateMonthlyIndexInfo();

            return true; // 성공적으로 완료
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 예외 발생 시 롤백
        }
    }

    public void uploadFileToFirebaseAndSave(EvidenceData newData, EvidenceData oldData) throws IOException {
        // 기존 파일 경로
        String oldFilePath = oldData.getFilePath();

        // 새로운 파일 경로
        String newFilePath = newData.getFilePath();

        // 임시 파일 생성
        File tempFile = File.createTempFile("temp", ".tmp");

        try {
            // Firebase에서 기존 파일 가져오기
            Blob oldBlob = storage.get(BlobId.of(bucketName, oldFilePath));
            if (oldBlob == null) {
                throw new IOException("File not found in Firebase Storage: " + oldFilePath);
            }

            // 기존 파일을 임시 파일로 다운로드
            oldBlob.downloadTo(tempFile.toPath());

            // 새로운 경로로 파일 업로드
            BlobId newBlobId = BlobId.of(bucketName, newFilePath);
            BlobInfo newBlobInfo = BlobInfo.newBuilder(newBlobId).setContentType(oldBlob.getContentType()).build();
            storage.create(newBlobInfo, Files.readAllBytes(tempFile.toPath()));

            // 파일 업로드 성공 후 데이터베이스 저장
            evidenceDataRepository.saveEvidenceData(newData);
            System.out.println("Database updated successfully after file upload.");

        } catch (Exception e) {
            throw new IOException("Error during file upload and save process: " + e.getMessage(), e);
        } finally {
            // 임시 파일 삭제
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException e) {
                System.err.println("Failed to delete temp file: " + tempFile.getAbsolutePath());
            }
        }
    }


    private List<SCMaster> getFilteredSCMasters() {
        return scMasterRepository.findAll().stream()
                .collect(Collectors.groupingBy(SCMaster::getCertificationYear))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(List.of());
    }

    private void modifyAndSaveDetails(
            List<CertifContent> certifContents,
            List<DefectManage> defectManages,
            List<OperationalStatus> operationalStatuses,
            List<EvidenceData> evidenceData) {

        // CertifContent 수정 및 저장
        List<CertifContent> newCertifContents = certifContents.stream()
                .map(this::createNewCertifContentForNextYear)
                .collect(Collectors.toList());
        certifContentRepository.saveAll(newCertifContents);

        // DefectManage 수정 및 저장
        List<DefectManage> newDefectManages = defectManages.stream()
                .map(this::createNewDefectManageForNextYear)
                .collect(Collectors.toList());
        defectManageRepository.saveAll(newDefectManages);

        // OperationalStatus 수정 및 저장
        List<OperationalStatus> newOperationalStatuses = operationalStatuses.stream()
                .map(this::createNewOperationalStatusForNextYear)
                .collect(Collectors.toList());
        operationalStatusRepository.saveAll(newOperationalStatuses);

        // EvidenceData 수정 및 저장 (파일 업로드 성공 후에 저장)
        for (EvidenceData oldData : evidenceData) {
            EvidenceData newData = createNewEvidenceDataForNextYear(oldData);

            // 파일 업로드가 성공한 후 데이터베이스에 저장
            try {
                uploadFileToFirebaseAndSave(newData, oldData);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // 각 데이터의 복사 및 수정 로직
    private SCMaster createNewMasterForNextYear(SCMaster oldMaster) {
        SCMaster newMaster = new SCMaster();

        // BaseEntity의 필드 복사
        newMaster.setClassificationCode(oldMaster.getClassificationCode());
        newMaster.setItemCode(oldMaster.getItemCode());
        newMaster.setUpdatedAt(oldMaster.getUpdatedAt());

        // SCMaster의 필드 복사
        newMaster.setCertificationTypeName(oldMaster.getCertificationTypeName());
        newMaster.setIsoDetails(oldMaster.getIsoDetails());
        newMaster.setPciDssDetails(oldMaster.getPciDssDetails());

        // 필요한 필드만 업데이트
        newMaster.setDocumentCode(incrementDocumentCode(oldMaster.getDocumentCode())); // DocumentCode 업데이트
        newMaster.setCertificationYear(oldMaster.getCertificationYear() + 1);

        return newMaster;
    }

    private CertifDetail createNewDetailForNextYear(CertifDetail oldDetail) {
        CertifDetail newDetail = new CertifDetail();

        // BaseEntity의 필드 복사
        newDetail.setClassificationCode(oldDetail.getClassificationCode());
        newDetail.setItemCode(oldDetail.getItemCode());
        newDetail.setUpdatedAt(oldDetail.getUpdatedAt());

        // CertifDetail의 필드 복사
        newDetail.setDetailItemTypeName(oldDetail.getDetailItemTypeName());

        // 필요한 필드만 업데이트
        newDetail.setDocumentCode(incrementDocumentCode(oldDetail.getDocumentCode()));
        newDetail.setDetailItemCode(incrementDetailItemCode(oldDetail.getDetailItemCode()));
        newDetail.setCertificationYear(oldDetail.getCertificationYear() + 1);
        newDetail.setCompleted(false); // 신규 생성된 항목이므로 완료 상태를 false로 설정

        return newDetail;
    }

    private CertifContent createNewCertifContentForNextYear(CertifContent oldContent) {
        CertifContent newContent = new CertifContent();

        // 기존 필드 복사
        newContent.setCertificationCriteria(oldContent.getCertificationCriteria());
        newContent.setKeyCheckpoints(oldContent.getKeyCheckpoints());
        newContent.setRelevantLaws(oldContent.getRelevantLaws());
        newContent.setUpdatedAt(oldContent.getUpdatedAt());
        newContent.setModifier(oldContent.getModifier());

        // DetailItemCode만 업데이트
        newContent.setDetailItemCode(incrementDetailItemCode(oldContent.getDetailItemCode()));

        return newContent;
    }

    private DefectManage createNewDefectManageForNextYear(DefectManage oldDefect) {
        DefectManage newDefect = new DefectManage();

        // 기존 필드 복사
        newDefect.setIsmsP(oldDefect.getIsmsP());
        newDefect.setIso27k(oldDefect.getIso27k());
        newDefect.setPciDss(oldDefect.getPciDss());
        newDefect.setUpdatedAt(oldDefect.getUpdatedAt());
        newDefect.setModifier(oldDefect.getModifier());

        // DetailItemCode만 업데이트
        newDefect.setDetailItemCode(incrementDetailItemCode(oldDefect.getDetailItemCode()));

        return newDefect;
    }

    private OperationalStatus createNewOperationalStatusForNextYear(OperationalStatus oldStatus) {
        OperationalStatus newStatus = new OperationalStatus();

        // 기존 필드 복사
        newStatus.setStatus(oldStatus.getStatus());
        newStatus.setRelatedDocument(oldStatus.getRelatedDocument());
        newStatus.setEvidenceName(oldStatus.getEvidenceName());
        newStatus.setUpdatedAt(oldStatus.getUpdatedAt());
        newStatus.setModifier(oldStatus.getModifier());

        // DetailItemCode만 업데이트
        newStatus.setDetailItemCode(incrementDetailItemCode(oldStatus.getDetailItemCode()));

        return newStatus;
    }

    private EvidenceData createNewEvidenceDataForNextYear(EvidenceData oldData) {
        EvidenceData newData = new EvidenceData();

        // 기존 필드 복사
        newData.setFileName(oldData.getFileName());
        newData.setFileSize(oldData.getFileSize());
        newData.setCreatedAt(oldData.getCreatedAt());
        newData.setCreator(oldData.getCreator());

        // DetailItemCode 갱신
        String newDetailItemCode = incrementDetailItemCode(oldData.getDetailItemCode());
        newData.setDetailItemCode(newDetailItemCode);

        // CertificationYear 갱신 (1 더하기)
        newData.setYear(oldData.getYear() + 1);

        // 새로운 경로 갱신
        String newFilePath = newData.getYear() + "/" + newDetailItemCode + "/" + newData.getFileName();
        newData.setFilePath(newFilePath);

        // 새로운 파일 키 생성 (UUID)
        newData.setFileKey(UUID.randomUUID());

        return newData;  // 파일 업로드 후에 저장
    }


    private void updateMonthlyIndexInfo() {
        // (1) 제일 최근 생성된 행을 List로 가져옴
        List<MonthlyIndexInfo> latestInfoList = monthlyIndexInfoRepository.findLatest();
        if (latestInfoList.isEmpty()) {
            return;
        }
        MonthlyIndexInfo latestInfo = latestInfoList.get(0);

        // (2) certificationYear 값에 +1한다
        MonthlyIndexInfo newInfo = new MonthlyIndexInfo();
        newInfo.setCertificationYear(latestInfo.getCertificationYear() + 1);

        // (3) 월별 인덱스 갱신
        Map<Integer, Integer> monthMapping = createMonthMapping();
        newInfo.setJan(monthMapping.get(1));
        newInfo.setFeb(monthMapping.get(2));
        newInfo.setMar(monthMapping.get(3));
        newInfo.setApr(monthMapping.get(4));
        newInfo.setMay(monthMapping.get(5));
        newInfo.setJun(monthMapping.get(6));
        newInfo.setJul(monthMapping.get(7));
        newInfo.setAug(monthMapping.get(8));
        newInfo.setSep(monthMapping.get(9));
        newInfo.setOct(monthMapping.get(10));
        newInfo.setNov(monthMapping.get(11));
        newInfo.setDecem(monthMapping.get(12)); // Decem 필드 사용

        // (4) MonthlyIndexInfoRepository에 수정된 리스트를 보내어 INSERT
        monthlyIndexInfoRepository.save(newInfo);
    }

    private Map<Integer, Integer> createMonthMapping() {
        // 현재 날짜 및 시간 설정 (서버의 현재 날짜와 시간)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(System.currentTimeMillis()));
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작하므로 +1

        // 월 매핑 생성
        Map<Integer, Integer> monthMapping = new HashMap<>();

        // 기준 달부터 1로 시작하여 12까지 매핑
        for (int i = 0; i < 12; i++) {
            int month = (currentMonth + i - 1) % 12 + 1;
            monthMapping.put(month, i + 1);
        }

        return monthMapping;
    }

    // 코드 증가 로직
    private String incrementDocumentCode(String documentCode) {
        String prefix = documentCode.substring(0, documentCode.length() - 4);
        int numericPart = Integer.parseInt(documentCode.substring(documentCode.length() - 4));
        numericPart += 100;
        return prefix + String.format("%04d", numericPart);
    }

    private String incrementDetailItemCode(String detailItemCode) {
        String prefix = detailItemCode.substring(0, detailItemCode.length() - 7);
        int numericPart = Integer.parseInt(detailItemCode.substring(detailItemCode.length() - 7));
        numericPart += 100000;
        return prefix + String.format("%07d", numericPart);
    }
}
