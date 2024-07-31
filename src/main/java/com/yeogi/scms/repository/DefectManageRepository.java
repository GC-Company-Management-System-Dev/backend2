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
}
