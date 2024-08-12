package com.yeogi.scms.service;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import com.yeogi.scms.domain.EvidenceData;
import com.yeogi.scms.repository.EvidenceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceDataService {

    private final EvidenceDataRepository evidenceDataRepository;

    @Value("${firebase.storage-url}")
    private String firebaseStorageUrl;

    @Autowired
    public EvidenceDataService(EvidenceDataRepository evidenceDataRepository) {
        this.evidenceDataRepository = evidenceDataRepository;
    }

    public List<EvidenceData> getEvidenceDataByDCode(String detailItemCode) {
        return evidenceDataRepository.findByDetailCode(detailItemCode);
    }

    private final String bucketName = "scms-1862c.appspot.com";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public String uploadFile(MultipartFile file, String detailItemCode) throws IOException {

        String fileName = file.getOriginalFilename();
        String folderPath = detailItemCode + "/" + fileName;  // detailItemCode 폴더 생성

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, folderPath).build();
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

    // 파일 업로드
    public String uploadFirebaseBucket(MultipartFile multipartFile, String fileName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseStorageUrl);

        Blob blob = bucket.create(fileName,
                multipartFile.getInputStream(), multipartFile.getContentType());

        return blob.getMediaLink(); // 파이어베이스에 저장된 파일 url
    }

    // 파일 삭제
    public void deleteFirebaseBucket(String key) {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseStorageUrl);

        bucket.get(key).delete();
    }

    public boolean saveEvidenceData(String detailItemCode, String fileName, Double fileSize, String filePath, String creator, String fileKey){
        try {
            // 현재 로그인한 사용자의 닉네임을 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            creator = authentication.getName(); // UserDetails의 getUsername()을 통해 가져온다

            evidenceDataRepository.save(fileName, fileSize, filePath, fileKey, creator, detailItemCode);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public void saveEvidenceData(EvidenceData evidenceData) {
        evidenceDataRepository.saveEvidenceData(evidenceData);
    }

    public void deleteEvidenceData(UUID fileKey) {
        evidenceDataRepository.deleteByFileKey(fileKey);
    }
}
