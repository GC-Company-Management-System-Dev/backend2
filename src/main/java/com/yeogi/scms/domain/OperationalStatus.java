package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class OperationalStatus {
    private Long sequence;
    private String detailItemCode; // 외래 키 필드를 직접 사용
    private String status;
    private String relatedDocument;
    private String evidenceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String modifier;

    // Getters and setters

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getDetailItemCode() {
        return detailItemCode;
    }

    public void setDetailItemCode(String detailItemCode) {
        this.detailItemCode = detailItemCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRelatedDocument() {
        return relatedDocument;
    }

    public void setRelatedDocument(String relatedDocument) {
        this.relatedDocument = relatedDocument;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public void setEvidenceName(String evidenceName) {
        this.evidenceName = evidenceName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}

