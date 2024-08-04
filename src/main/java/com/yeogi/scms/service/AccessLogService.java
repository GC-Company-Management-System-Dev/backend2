package com.yeogi.scms.service;

import com.yeogi.scms.domain.AccessLog;
import com.yeogi.scms.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    @Autowired
    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public List<AccessLog> getAllLogs(int page, int size) {
        int offset = (page - 1) * size;
        return accessLogRepository.findAllWithPagination(offset, size);
    }

    public int getTotalLogCount() {
        return accessLogRepository.count();
    }

    public void saveAccessLog(AccessLog accessLog) {
        accessLogRepository.save(accessLog);
    }
}
