package com.yeogi.scms.domain;


import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Data
public class EvidenceData {
    private UUID fileKey;
    private String detailItemCode;
    private String fileName;
    private double fileSize;
    private String filePath;
    private Timestamp createdAt;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }
}

