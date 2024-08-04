package com.yeogi.scms.repository;

import com.yeogi.scms.domain.CertifContent;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CertifContentRepository {
    private final JdbcTemplate jdbcTemplate;

    public CertifContentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CertifContent> findByDetailCode(String detailItemCode) {
        String sql = "SELECT * FROM Certification_Item_Content WHERE Detail_Item_Code = ?";
        return jdbcTemplate.query(sql, new Object[]{detailItemCode}, new RowMapper<CertifContent>() {
            @Override
            public CertifContent mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapCertifContent(rs);
            }
        });
    }

    public void updateCertifContent(String detailItemCode, String certificationCriteria, String keyCheckpoints, String relevantLaws) {
        String sql = "UPDATE Certification_Item_Content SET Certification_Criteria = ?, Key_Checkpoints = ?, Relevant_Laws = ?, Updated_At = NOW(), Modifier = ? WHERE Detail_Item_Code = ?";
        jdbcTemplate.update(sql, certificationCriteria, keyCheckpoints, relevantLaws, detailItemCode);
    }
}
