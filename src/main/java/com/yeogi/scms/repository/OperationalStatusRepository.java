package com.yeogi.scms.repository;

import com.yeogi.scms.domain.OperationalStatus;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OperationalStatusRepository {
    private final JdbcTemplate jdbcTemplate;

    public OperationalStatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OperationalStatus> findByDetailCode(String detailItemCode) {
        String sql = "SELECT * FROM Operational_Status WHERE Detail_Item_Code = ?";
        return jdbcTemplate.query(sql, new Object[]{detailItemCode}, new RowMapper<OperationalStatus>() {
            @Override
            public OperationalStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapOperationalStatus(rs);
            }
        });
    }

    public void updateOperationalStatus(String detailItemCode, String status, String relatedDocument, String evidenceName, String modifier) {
        String sql = "UPDATE Operational_Status SET Status = ?, Related_Document = ?, Evidence_Name = ?, Updated_At = CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul'), Modifier = ? WHERE Detail_Item_Code = ?";

        jdbcTemplate.update(sql, status, relatedDocument, evidenceName, modifier, detailItemCode);
    }
}
