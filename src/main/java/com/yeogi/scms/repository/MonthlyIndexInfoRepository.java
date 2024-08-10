package com.yeogi.scms.repository;

import com.yeogi.scms.domain.MonthlyIndexInfo;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public List<MonthlyIndexInfo> findLatest() {
        String sql = "SELECT * FROM Monthly_Index_Info ORDER BY Certification_Year DESC LIMIT 1";
        return jdbcTemplate.query(sql, new RowMapper<MonthlyIndexInfo>() {
            @Override
            public MonthlyIndexInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapMonthlyIndexInfo(rs);
            }
        });
    }

    public void save(MonthlyIndexInfo monthlyIndexInfo) {
        String sql = "INSERT INTO Monthly_Index_Info " +
                "(Certification_Year, Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Decem) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                monthlyIndexInfo.getCertificationYear(),
                monthlyIndexInfo.getJan(),
                monthlyIndexInfo.getFeb(),
                monthlyIndexInfo.getMar(),
                monthlyIndexInfo.getApr(),
                monthlyIndexInfo.getMay(),
                monthlyIndexInfo.getJun(),
                monthlyIndexInfo.getJul(),
                monthlyIndexInfo.getAug(),
                monthlyIndexInfo.getSep(),
                monthlyIndexInfo.getOct(),
                monthlyIndexInfo.getNov(),
                monthlyIndexInfo.getDecem()
        );
    }
}
