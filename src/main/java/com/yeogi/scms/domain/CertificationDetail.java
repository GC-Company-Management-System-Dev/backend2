package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class CertificationDetail {
    private String detailItemCode;
    private String securityCertificationMasterCode;
    private String classificationCode;
    private int certificationYear;
    private String itemCode;
    private String detailItemTypeName;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int monthlyIndex;

    // Getters and setters

    public String getDetailItemCode() {
        return detailItemCode;
    }

    public void setDetailItemCode(String detailItemCode) {
        this.detailItemCode = detailItemCode;
    }

    public String getSecurityCertificationMasterCode() {
        return securityCertificationMasterCode;
    }

    public void setSecurityCertificationMasterCode(String securityCertificationMasterCode) {
        this.securityCertificationMasterCode = securityCertificationMasterCode;
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

    public String getDetailItemTypeName() {
        return detailItemTypeName;
    }

    public void setDetailItemTypeName(String detailItemTypeName) {
        this.detailItemTypeName = detailItemTypeName;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
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

    public int getMonthlyIndex() {
        return monthlyIndex;
    }

    public void setMonthlyIndex(int monthlyIndex) {
        this.monthlyIndex = monthlyIndex;
    }
}

