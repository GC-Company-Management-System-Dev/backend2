package com.yeogi.scms.repository;

import com.yeogi.scms.domain.SCMaster;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SCMasterRepository {
    private final JdbcTemplate jdbcTemplate;

    public SCMasterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SCMaster> findBySCCode(String sccode) {
        String sql = "SELECT * FROM Security_Certification_Master WHERE Classification_Code LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{sccode + '%'}, new RowMapper<SCMaster>() {
            @Override
            public SCMaster mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapSCMaster(rs);
            }
        });
    }
}
