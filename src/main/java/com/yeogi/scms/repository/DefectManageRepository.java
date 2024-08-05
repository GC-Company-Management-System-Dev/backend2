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

    public void updateDefectManage(String detailItemCode, String ismsP, String iso27k, String pciDss, String modifier) {
        String sql = "UPDATE Defect_Management SET ISMS_P = ?, ISO27K = ?, PCI_DSS = ?, Updated_At = CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul'), Modifier = ? WHERE Detail_Item_Code = ?";

        System.out.println("Executing SQL: " + sql);
        System.out.println("With parameters: " + ismsP + ", " + iso27k + ", " + pciDss + ", " + modifier + ", " + detailItemCode);


        jdbcTemplate.update(sql, ismsP, iso27k, pciDss, modifier, detailItemCode);
    }
}
