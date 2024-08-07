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

    public List<CertifDetail> findByDocCode(String documentCode) {
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

    // 작성 완료 체크 박스
    public int updateCompletionStatus(String detailItemCode, boolean completed) {
        String sql = "UPDATE Certification_Detail_Item SET Completed = ? WHERE Detail_Item_Code = ?";
        return jdbcTemplate.update(sql, completed, detailItemCode);
    }

    // 인증 연도 가져오기
    public Integer findCertificationYearByDetailItemCode(String detailItemCode) {
        String sql = "SELECT Certification_Year FROM Certification_Detail_Item WHERE Detail_Item_Code = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{detailItemCode}, Integer.class);
    }

    // 월별 인덱스 업데이트
    public int updateMonthlyIndex(String detailItemCode, Integer monthlyIndex) {
        String sql = "UPDATE Certification_Detail_Item SET Monthly_Index = ? WHERE Detail_Item_Code = ?";
        return jdbcTemplate.update(sql, monthlyIndex, detailItemCode);
    }

    // 월별 인덱스 삭제
    public int deleteMonthlyIndex(String detailItemCode) {
        String sql = "UPDATE Certification_Detail_Item SET Monthly_Index = NULL WHERE Detail_Item_Code = ?";
        return jdbcTemplate.update(sql, detailItemCode);
    }
}
