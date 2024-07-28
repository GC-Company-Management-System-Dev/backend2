package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class DownloadLog {
    private Long logSequence;
    private String accessId;
    private String fileName;
    private String fileKey;
    private LocalDateTime timestamp;

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

