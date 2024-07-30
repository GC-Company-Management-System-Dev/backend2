package com.yeogi.scms.domain;

public class SCMaster extends BaseEntity {
    private String certificationTypeName;
    private String isoDetails;
    private String pciDssDetails;

    // Getters and setters

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
}
