package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class DetailEntity {
    private Long sequence;
    private String detailItemCode;
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
