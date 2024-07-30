package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class BaseEntity {
    private String documentCode;
    private String classificationCode;
    private int certificationYear;
    private String itemCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public int getCertificationYear() {
        return certificationYear;
    }

    public void setCertificationYear(int certificationYear) {
        this.certificationYear = certificationYear;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
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
}
