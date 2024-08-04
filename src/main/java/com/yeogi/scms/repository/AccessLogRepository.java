package com.yeogi.scms.repository;

import com.yeogi.scms.domain.AccessLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.yeogi.scms.util.RowMapperUtil;

import java.util.List;

@Repository
public class AccessLogRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(AccessLog accessLog) {
        String sql = "INSERT INTO Access_Log (Access_ID, Action, Access_Path, Timestamp) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, accessLog.getAccessId(), accessLog.getAction(), accessLog.getAccessPath(), accessLog.getTimestamp());
    }

    public List<AccessLog> findAllWithPagination(int offset, int limit) {
        String sql = "SELECT Log_Sequence, Access_ID, Action, Access_Path, Timestamp FROM Access_Log ORDER BY Log_Sequence DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{limit, offset}, (rs, rowNum) -> RowMapperUtil.mapAccessLog(rs));
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM Access_Log";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
