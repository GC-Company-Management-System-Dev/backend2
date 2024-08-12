package com.yeogi.scms.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DownloadLog {
    private Long logSequence;
    private String accessId;
    private String fileName;
    private String fileKey;
    private Timestamp timestamp;

    // Getters and setters

    public Long getLogSequence() {
        return logSequence;
    }

    public void setLogSequence(Long logSequence) {
        this.logSequence = logSequence;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

