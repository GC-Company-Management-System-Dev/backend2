package com.yeogi.scms.domain;

import java.time.LocalDateTime;

public class AccessLog {
    private Long logSequence; // Log_Sequence 열에 대응
    private String accessId;  // Access_ID 열에 대응
    private String action;    // Action 열에 대응
    private String accessPath; // Access_Path 열에 대응
    private LocalDateTime timestamp; // Timestamp 열에 대응

    // 기본 생성자
    public AccessLog() {}

    // 각 속성에 대한 게터와 세터
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAccessPath() {
        return accessPath;
    }

    public void setAccessPath(String accessPath) {
        this.accessPath = accessPath;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

