package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class SecurityCertificationMaster {
    private String documentCode;
    private String classificationCode;
    private int certificationYear;
    private String itemCode;
    private String certificationTypeName;
    private String isoDetails;
    private String pciDssDetails;
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

    public String getCertificationTypeName() {
        return certificationTypeName;
    }

    public void setCertificationTypeName(String certificationTypeName) {
        this.certificationTypeName = certificationTypeName;
    }

    public String getIsoDetails() {
        return isoDetails;
    }

    public void setIsoDetails(String isoDetails) {
        this.isoDetails = isoDetails;
    }

    public String getPciDssDetails() {
        return pciDssDetails;
    }

    public void setPciDssDetails(String pciDssDetails) {
        this.pciDssDetails = pciDssDetails;
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

