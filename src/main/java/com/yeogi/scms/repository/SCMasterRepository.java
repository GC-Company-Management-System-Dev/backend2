package com.yeogi.scms.repository;

import com.yeogi.scms.domain.SCMaster;
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

    private static final class SCMasterRowMapper implements RowMapper<SCMaster> {
        @Override
        public SCMaster mapRow(ResultSet rs, int rowNum) throws SQLException {
            SCMaster master = new SCMaster();
            master.setDocumentCode(rs.getString("Document_Code"));
            master.setClassificationCode(rs.getString("Classification_Code"));
            master.setCertificationYear(rs.getInt("Certification_Year"));
            master.setItemCode(rs.getString("Item_Code"));
            master.setCertificationTypeName(rs.getString("Certification_Type_Name"));
            master.setIsoDetails(rs.getString("ISO_Details"));
            master.setPciDssDetails(rs.getString("PCI_DSS_Details"));
            master.setCreatedAt(rs.getTimestamp("Created_At").toLocalDateTime());
            master.setUpdatedAt(rs.getTimestamp("Updated_At") != null ? rs.getTimestamp("Updated_At").toLocalDateTime() : null);
            return master;
        }
    }

    public List<SCMaster> findByDocumentCode(String prefix) {
        String sql = "SELECT * FROM Security_Certification_Master WHERE Classification_Code LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{prefix + '%'}, new SCMasterRowMapper());
    }
}
