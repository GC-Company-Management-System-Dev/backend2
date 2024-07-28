package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class DefectManagement {
    private Long sequence;
    private String detailItemCode;
    private String ismsP;
    private String iso27k;
    private String pciDss;
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

    public String getIsmsP() {
        return ismsP;
    }

    public void setIsmsP(String ismsP) {
        this.ismsP = ismsP;
    }

    public String getIso27k() {
        return iso27k;
    }

    public void setIso27k(String iso27k) {
        this.iso27k = iso27k;
    }

    public String getPciDss() {
        return pciDss;
    }

    public void setPciDss(String pciDss) {
        this.pciDss = pciDss;
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

