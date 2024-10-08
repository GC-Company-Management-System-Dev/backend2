package com.yeogi.scms.repository;

import com.yeogi.scms.domain.DefectManage;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DefectManageRepository {
    private final JdbcTemplate jdbcTemplate;

    public DefectManageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DefectManage> findByDetailCode(String detailItemCode) {
        String sql = "SELECT * FROM Defect_Management WHERE Detail_Item_Code = ?";
        return jdbcTemplate.query(sql, new Object[]{detailItemCode}, new RowMapper<DefectManage>() {
            @Override
            public DefectManage mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapDefectManage(rs);
            }
        });
    }

    public void updateDefectManage(String detailItemCode, String certificationType, String defectContent, String modifier) {
        String sql = "UPDATE Defect_Management SET Updated_At = CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul'), ";

        switch (certificationType) {
            case "ISMS-P":
                sql += "ISMS_P = ?";
                break;
            case "ISO 27K":
                sql += "ISO27K = ?";
                break;
            case "PCI-DSS":
                sql += "PCI_DSS = ?";
                break;
            default:
                throw new IllegalArgumentException("Unknown certification type: " + certificationType);
        }

        sql += ", Modifier = ? WHERE Detail_Item_Code = ?";

        System.out.println("Executing SQL: " + sql);
        System.out.println("With parameters: " + defectContent + ", " + modifier + ", " + detailItemCode);

        try {
            jdbcTemplate.update(sql, defectContent, modifier, detailItemCode);
        } catch (Exception e) {
            System.err.println("Error executing SQL: " + e.getMessage());
            throw e;
        }
    }

    // saveAll 메서드 추가
    public void saveAll(List<DefectManage> defects) {
        String sql = "INSERT INTO Defect_Management " +
                "(Detail_Item_Code, ISMS_P, ISO27K, PCI_DSS, Modifier, Updated_At) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, defects, defects.size(), (ps, defect) -> {
            ps.setString(1, defect.getDetailItemCode());
            ps.setString(2, defect.getIsmsP());
            ps.setString(3, defect.getIso27k());
            ps.setString(4, defect.getPciDss());
            ps.setString(5, defect.getModifier());
            ps.setTimestamp(6, defect.getUpdatedAt());
        });
    }

}
