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

        String fileName = file.getOriginalFilename();
        String folderPath = detailItemCode + "/" + fileName;  // detailItemCode 폴더 생성

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, folderPath)
                .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))  // 파일을 공개로 설정
                .build();
        storage.create(blobInfo, file.getBytes());

        // 현재 로그인한 사용자의 닉네임을 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String creator = authentication.getName(); // UserDetails의 getUsername()을 통해 가져온다


        // DB에 파일 정보 저장
        EvidenceData evidenceData = new EvidenceData();
        evidenceData.setFileKey(UUID.randomUUID());  // UUID로 파일 키 생성
        evidenceData.setDetailItemCode(detailItemCode);
        evidenceData.setFileName(fileName);
        evidenceData.setFileSize(file.getSize());
        evidenceData.setFilePath(folderPath);
        evidenceData.setCreatedAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Seoul"))));  // 한국 시간으로 설정
        evidenceData.setCreator(creator);

        evidenceDataRepository.saveEvidenceData(evidenceData);

        return fileName;
    }

    // 파일 정보 조회
    public List<Map<String, Object>> listFilesInFolder(String detailItemCode) {
        List<Map<String, Object>> files = new ArrayList<>();
        String folderPath = detailItemCode + "/";

        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(folderPath));

        for (Blob blob : blobs.iterateAll()) {
            Map<String, Object> fileData = new HashMap<>();
            fileData.put("name", blob.getName().substring(folderPath.length()));
            fileData.put("size", blob.getSize());
            fileData.put("url", blob.getMediaLink());

            files.add(fileData);
        }

        return files;
    }

    // detailItemCode에 해당하는 증적자료 파일 목록을 가져오는 메소드
    public List<EvidenceData> getEvidenceDataByDCode(String detailItemCode) {
        return evidenceDataRepository.findByDetailCode(detailItemCode);
    }

    // detailItemCode에 해당하는 가장 최근에 수정된 일시와 변경자를 가져오는 메소드
    public Map<String, Object> getLatestModificationInfo(String detailItemCode) {
        return evidenceDataRepository.findLatestModificationInfoByDetailItemCode(detailItemCode);
    }

    // 파일 삭제
    @Transactional
    public boolean deleteFile(String detailItemCode, String fileName) {

        try {
            // Firebase Storage에서 파일 삭제
            String filePath = detailItemCode + "/" + fileName;
//        StorageClient.getInstance().bucket().get(filePath).delete();

            Bucket bucket = StorageClient.getInstance().bucket(firebaseStorageUrl);
            bucket.get(filePath).delete();

            // DB에서 파일 정보 삭제
            evidenceDataRepository.deleteEvidenceData(detailItemCode, fileName);

            // Debugging statements
            System.out.println("sdic: " + detailItemCode);
            System.out.println("sfn: " + fileName);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}

//    // 파일 삭제 메서드
//    public void deleteFile(UUID fileKey) {
//        // 1. 데이터베이스에서 파일 정보 삭제
//        evidenceDataRepository.deleteEvidenceData(fileKey);
//
//        // 2. Firebase Storage에서 파일 삭제
//        StorageClient storageClient = StorageClient.getInstance();
//        Bucket bucket = storageClient.bucket();
//
//        // Firebase에 저장된 파일 경로 가져오기
//        String filePath = evidenceDataRepository.findFilePathByFileKey(fileKey);
//
//        if (filePath != null) {
//            // Firebase Storage에서 파일 삭제
//            Blob blob = bucket.get(filePath);
//            if (blob != null) {
//                blob.delete();
//            }
//        }
//    }

//    public void deleteFile(String fileName, String detailItemCode) {
//        String folderPath = detailItemCode + "/" + fileName;
//
//        // Firebase Storage에서 파일 삭제
//        BlobId blobId = BlobId.of(bucketName, folderPath);
//        boolean deleted = storage.delete(blobId);
//
//        if (deleted) {
//            // DB에서 파일 정보 삭제
//            evidenceDataRepository.deleteByFileNameAndDetailItemCode(fileName, detailItemCode);
//        } else {
//            throw new RuntimeException("파일 삭제 실패: " + fileName);
//        }
//    }
//
//
//    public void deleteFile(UUID fileKey, String detailItemCode, String fileName) {
//        // Firebase Storage에서 파일 삭제
//        String folderPath = detailItemCode + "/" + fileName;
//        BlobId blobId = BlobId.of(bucketName, folderPath);
//        boolean deleted = storage.delete(blobId);
//
//        if (deleted) {
//            // DB에서 파일 정보 삭제
//            evidenceDataRepository.deleteByFileKey(fileKey);
//        } else {
//            throw new RuntimeException("파일 삭제 실패");
//        }
//    }
//
//
//
//    // 파일 업로드
//    public String uploadFirebaseBucket(MultipartFile multipartFile, String fileName) throws IOException {
//        Bucket bucket = StorageClient.getInstance().bucket(firebaseStorageUrl);
//
//        Blob blob = bucket.create(fileName,
//                multipartFile.getInputStream(), multipartFile.getContentType());
//
//        return blob.getMediaLink(); // 파이어베이스에 저장된 파일 url
//    }
//
//    // 파일 삭제
//    public void deleteFirebaseBucket(String key) {
//        Bucket bucket = StorageClient.getInstance().bucket(firebaseStorageUrl);
//
//        bucket.get(key).delete();
//    }
//
//    public boolean saveEvidenceData(String detailItemCode, String fileName, Double fileSize, String filePath, String creator, String fileKey){
//        try {
//            // 현재 로그인한 사용자의 닉네임을 가져오기
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            creator = authentication.getName(); // UserDetails의 getUsername()을 통해 가져온다
//
//            evidenceDataRepository.save(fileName, fileSize, filePath, fileKey, creator, detailItemCode);
//            return true;
//        } catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//    public void saveEvidenceData(EvidenceData evidenceData) {
//        evidenceDataRepository.saveEvidenceData(evidenceData);
//    }
//
//    public void deleteEvidenceData(UUID fileKey) {
//        evidenceDataRepository.deleteByFileKey(fileKey);
//    }

