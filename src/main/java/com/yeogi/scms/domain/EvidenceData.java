package com.yeogi.scms.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class EvidenceData {
    private UUID fileKey;
    private String detailItemCode; // 외래 키 필드를 직접 사용
    private String fileName;
    private double fileSize;
    private String filePath;
    private LocalDateTime createdAt;
    private String creator;

    // Getters and setters

    public UUID getFileKey() {
        return fileKey;
    }

    public void setFileKey(UUID fileKey) {
        this.fileKey = fileKey;
    }

    public String getDetailItemCode() {
        return detailItemCode;
    }

    public void setDetailItemCode(String detailItemCode) {
        this.detailItemCode = detailItemCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}

