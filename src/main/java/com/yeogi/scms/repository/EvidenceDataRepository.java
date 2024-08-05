package com.yeogi.scms.repository;

import com.yeogi.scms.domain.EvidenceData;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Repository
public class EvidenceDataRepository {
    private final JdbcTemplate jdbcTemplate;

    public EvidenceDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EvidenceData> findByDetailCode(String detailItemCode) {
        String sql = "SELECT * FROM Evidence_Data WHERE Detail_Item_Code = ?";
        return jdbcTemplate.query(sql, new Object[]{detailItemCode}, new RowMapper<EvidenceData>() {
            @Override
            public EvidenceData mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapEvidenceData(rs);
            }
        });
    }
}
