package com.yeogi.scms.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import com.yeogi.scms.domain.EvidenceData;
import com.yeogi.scms.domain.LoginAccount;
import com.yeogi.scms.repository.EvidenceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class EvidenceDataService {

    private final EvidenceDataRepository evidenceDataRepository;

    @Value("${firebase.storage-url}")
    private String firebaseStorageUrl;

    @Autowired
    public EvidenceDataService(EvidenceDataRepository evidenceDataRepository) {
        this.evidenceDataRepository = evidenceDataRepository;
    }

    private final String bucketName = "scms-1862c.appspot.com";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public String uploadFile(MultipartFile file, String detailItemCode) throws IOException {

        // 4, 5번째 문자를 추출하여 year 계산
        int year = 0;  // 기본값 설정
        if (detailItemCode != null && detailItemCode.length() >= 5) {
            try {
                String yearPart = detailItemCode.substring(3, 5); // 4, 5번째 문자 추출
                year = Integer.parseInt("20" + yearPart);  // 예: "27" -> 2027
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid detailItemCode format: Unable to extract year.");
            }
        } else {
            throw new IllegalArgumentException("detailItemCode is too short to extract year.");
        }

        String fileName = file.getOriginalFilename();
        String folderPath = year + "/" + detailItemCode + "/" + fileName;  // detailItemCode 폴더 생성

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, folderPath)
                .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))  // 파일을 공개로 설정
                .build();
        storage.create(blobInfo, file.getBytes());

        // 현재 로그인한 사용자의 닉네임을 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String creator = userDetails.getNickname();


        // DB에 파일 정보 저장
        EvidenceData evidenceData = new EvidenceData();
        evidenceData.setFileKey(UUID.randomUUID());  // UUID로 파일 키 생성
        evidenceData.setDetailItemCode(detailItemCode);
        evidenceData.setFileName(fileName);
        evidenceData.setFileSize(file.getSize());
        evidenceData.setFilePath(folderPath);
        evidenceData.setCreatedAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Seoul"))));  // 한국 시간으로 설정
        evidenceData.setCreator(creator);
        evidenceData.setYear(year);

        evidenceDataRepository.saveEvidenceData(evidenceData);

        return fileName;
    }

    // 파일 정보 조회

    // Firebase 스토리지에서 파일 조회
//    public List<Map<String, Object>> listFilesInFolder(String detailItemCode) {
//        List<Map<String, Object>> files = new ArrayList<>();
//        String folderPath = detailItemCode + "/";
//
//        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(folderPath));
//
//        for (Blob blob : blobs.iterateAll()) {
//            Map<String, Object> fileData = new HashMap<>();
//            fileData.put("name", blob.getName().substring(folderPath.length()));
//            fileData.put("size", blob.getSize());
//            fileData.put("url", blob.getMediaLink());
//
//            files.add(fileData);
//        }
//
//        return files;
//    }

    // detailItemCode에 해당하는 증적자료 파일 목록을 가져오는 메소드
    public List<EvidenceData> getEvidenceDataByDCode(String detailItemCode) {
        return evidenceDataRepository.findByDetailCode(detailItemCode);
    }

    // detailItemCode에 해당하는 가장 최근에 수정된 일시와 변경자를 가져오는 메소드
    public Map<String, Object> getLatestModificationInfo(String detailItemCode) {
        return evidenceDataRepository.findLatestModificationInfoByDetailItemCode(detailItemCode);
    }

    // 파일 삭제
    public void deleteFile(String detailItemCode, String fileName) {

        // 4, 5번째 문자를 추출하여 year 계산
        int year = 0;  // 기본값 설정
        if (detailItemCode != null && detailItemCode.length() >= 5) {
            try {
                String yearPart = detailItemCode.substring(3, 5); // 4, 5번째 문자 추출
                year = Integer.parseInt("20" + yearPart);  // 예: "27" -> 2027
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid detailItemCode format: Unable to extract year.");
            }
        } else {
            throw new IllegalArgumentException("detailItemCode is too short to extract year.");
        }

        // Firebase Storage에서 파일 삭제
        String filePath = year + "/" + detailItemCode + "/" + fileName;
        BlobId blobId = BlobId.of(bucketName, filePath);
        boolean deleted = storage.delete(blobId);

        // DB에서 파일 정보 삭제
        evidenceDataRepository.deleteByDetailItemCodeAndFileName(detailItemCode, fileName);

        if (deleted) {

            // Debugging statements
            System.out.println("sdic: " + detailItemCode);
            System.out.println("sfn: " + fileName);

        } else {
            throw new RuntimeException("Failed to delete file from Firebase Storage.");
        }
    }

    // 파일 이름이 없거나 파일 크기가 0인 파일 삭제
    public void deleteEmptyFiles(String detailItemCode) {
        evidenceDataRepository.deleteEmptyFiles(detailItemCode);
    }
}

