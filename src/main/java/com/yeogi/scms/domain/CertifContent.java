package com.yeogi.scms.domain;

public class CertifContent extends DetailEntity {
    private String certificationCriteria;
    private String keyCheckpoints;
    private String relevantLaws;

    // Getters and setters

    public String getCertificationCriteria() {
        return certificationCriteria;
    }

    public void setCertificationCriteria(String certificationCriteria) {
        this.certificationCriteria = certificationCriteria;
    }

    public String getKeyCheckpoints() {
        return keyCheckpoints;
    }

    public void setKeyCheckpoints(String keyCheckpoints) {
        this.keyCheckpoints = keyCheckpoints;
    }

    public String getRelevantLaws() {
        return relevantLaws;
    }

    public void setRelevantLaws(String relevantLaws) {
        this.relevantLaws = relevantLaws;
    }
}
