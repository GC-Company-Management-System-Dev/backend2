package com.yeogi.scms.domain;

public class DefectManage extends DetailEntity {
    private String ismsP;
    private String iso27k;
    private String pciDss;

    // Getters and setters

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
}
