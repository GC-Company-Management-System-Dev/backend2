package com.yeogi.scms.repository;

import com.yeogi.scms.domain.MonthlyIndexInfo;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MonthlyIndexInfoRepository {
    private final JdbcTemplate jdbcTemplate;

    public MonthlyIndexInfoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MonthlyIndexInfo findByCertificationYear(Integer certificationYear) {
        String sql = "SELECT * FROM Monthly_Index_Info WHERE Certification_Year = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{certificationYear}, new RowMapper<MonthlyIndexInfo>() {
            @Override
            public MonthlyIndexInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapMonthlyIndexInfo(rs);
            }
        });
    }
}
