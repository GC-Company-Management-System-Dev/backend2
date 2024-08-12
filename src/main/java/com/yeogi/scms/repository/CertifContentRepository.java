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

    public void updateCertifContent(String detailItemCode, String certificationCriteria, String keyCheckpoints, String relevantLaws, String modifier) {
        String sql = "UPDATE Certification_Item_Content SET Certification_Criteria = ?, Key_Checkpoints = ?, Relevant_Laws = ?, Updated_At = CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul'), Modifier = ? WHERE Detail_Item_Code = ?";
        System.out.println("Executing SQL: " + sql);
        System.out.println("With parameters: " + certificationCriteria + ", " + keyCheckpoints + ", " + relevantLaws + ", " + modifier + ", " + detailItemCode);

        jdbcTemplate.update(sql, certificationCriteria, keyCheckpoints, relevantLaws, modifier, detailItemCode);
    }

    public void saveAll(List<CertifContent> contents) {
        String sql = "INSERT INTO Certification_Item_Content " +
                "(Detail_Item_Code, Certification_Criteria, Key_Checkpoints, Relevant_Laws, Modifier, Updated_At) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, contents, contents.size(), (ps, content) -> {
            ps.setString(1, content.getDetailItemCode());
            ps.setString(2, content.getCertificationCriteria());
            ps.setString(3, content.getKeyCheckpoints());
            ps.setString(4, content.getRelevantLaws());
            ps.setString(5, content.getModifier());
            ps.setTimestamp(6, content.getUpdatedAt());
        });
    }

}
