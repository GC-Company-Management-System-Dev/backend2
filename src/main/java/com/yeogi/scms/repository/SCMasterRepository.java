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

    public List<SCMaster> findAll() {
        String sql = "SELECT * FROM Security_Certification_Master";
        return jdbcTemplate.query(sql, new RowMapper<SCMaster>() {
            @Override
            public SCMaster mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapSCMaster(rs);
            }
        });
    }

    public void updateDetails(String documentCode, String isoDetails, String pciDssDetails) {
        String sql = "UPDATE Security_Certification_Master SET ISO_Details = ?, PCI_DSS_Details = ? WHERE Document_Code = ?";
        System.out.println("Updating details for documentCode: " + documentCode);
        System.out.println("ISO Details: " + isoDetails);
        System.out.println("PCI DSS Details: " + pciDssDetails);
        jdbcTemplate.update(sql, isoDetails, pciDssDetails, documentCode);
    }

    public void saveAll(List<SCMaster> masters) {
        String sql = "INSERT INTO Security_Certification_Master " +
                "(Document_Code, Classification_Code, Certification_Year, Item_Code, " +
                "Certification_Type_Name, ISO_Details, PCI_DSS_Details, Updated_At) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, masters, masters.size(), (ps, master) -> {
            ps.setString(1, master.getDocumentCode());
            ps.setString(2, master.getClassificationCode());
            ps.setInt(3, master.getCertificationYear());
            ps.setString(4, master.getItemCode());
            ps.setString(5, master.getCertificationTypeName());
            ps.setString(6, master.getIsoDetails());
            ps.setString(7, master.getPciDssDetails());
            ps.setTimestamp(8, master.getUpdatedAt());
        });
    }

}
