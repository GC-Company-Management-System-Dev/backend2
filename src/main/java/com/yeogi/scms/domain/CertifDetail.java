package com.yeogi.scms.domain;

public class CertifDetail extends BaseEntity {
    private String detailItemCode;
    private String detailItemTypeName;
    private boolean completed;
    private int monthlyIndex;

    // Getters and setters

    public String getDetailItemCode() {
        return detailItemCode;
    }

    public void setDetailItemCode(String detailItemCode) {
        this.detailItemCode = detailItemCode;
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

    public int getMonthlyIndex() {
        return monthlyIndex;
    }

    public void setMonthlyIndex(int monthlyIndex) {
        this.monthlyIndex = monthlyIndex;
    }
}
