package com.yeogi.scms.repository;

import com.yeogi.scms.domain.CertifDetail;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CertifDetailRepository {
    private final JdbcTemplate jdbcTemplate;

    public CertifDetailRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CertifDetail> findByDocuCode(String documentCode) {
        String sql = "SELECT * FROM Certification_Detail_Item WHERE Document_Code = ?";
        return jdbcTemplate.query(sql, new Object[]{documentCode}, new RowMapper<CertifDetail>() {
            @Override
            public CertifDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapCertifDetail(rs);
            }
        });
    }

    public List<CertifDetail> findByDetailCode(String detailItemCode) {
        String sql = "SELECT * FROM Certification_Detail_Item WHERE Detail_Item_Code = ?";
        return jdbcTemplate.query(sql, new Object[]{detailItemCode}, new RowMapper<CertifDetail>() {
            @Override
            public CertifDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapCertifDetail(rs);
            }
        });
    }
}
