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

    // 특정 인증 년도의 MonthlyIndexInfo를 가져오는 메서드
    public List<MonthlyIndexInfo> findByCertificationYear(int certificationYear) {
        String sql = "SELECT * FROM Monthly_Index_Info WHERE Certification_Year = ?";
        return jdbcTemplate.query(sql, new Object[]{certificationYear}, new RowMapper<MonthlyIndexInfo>() {
            @Override
            public MonthlyIndexInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                MonthlyIndexInfo info = new MonthlyIndexInfo();
                info.setCertificationYear(rs.getInt("Certification_Year"));
                info.setJan(rs.getInt("Jan"));
                info.setFeb(rs.getInt("Feb"));
                info.setMar(rs.getInt("Mar"));
                info.setApr(rs.getInt("Apr"));
                info.setMay(rs.getInt("May"));
                info.setJun(rs.getInt("Jun"));
                info.setJul(rs.getInt("Jul"));
                info.setAug(rs.getInt("Aug"));
                info.setSep(rs.getInt("Sep"));
                info.setOct(rs.getInt("Oct"));
                info.setNov(rs.getInt("Nov"));
                info.setDecem(rs.getInt("Decem"));
                info.setCreatedAt(rs.getTimestamp("Created_At"));
                return info;
            }
        });
    }
}
