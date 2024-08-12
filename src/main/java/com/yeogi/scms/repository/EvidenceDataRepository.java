package com.yeogi.scms.repository;

import com.yeogi.scms.domain.EvidenceData;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class EvidenceDataRepository {
    private final JdbcTemplate jdbcTemplate;

    public EvidenceDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EvidenceData> findByDetailCode(String detailItemCode) {
        String sql = "SELECT * FROM Evidence_Data WHERE Detail_Item_Code = ?";
        return jdbcTemplate.query(sql, new Object[]{detailItemCode}, new RowMapper<EvidenceData>() {
            @Override
            public EvidenceData mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapEvidenceData(rs);
            }
        });
    }

    public void save(EvidenceData evidenceData) {
        String sql = "INSERT INTO Evidence_Data (Detail_Item_Code, File_Name, File_Size, File_Path, File_Key, Created_At, Creator) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, evidenceData.getDetailItemCode(), evidenceData.getFileName(), evidenceData.getFileSize(),
                evidenceData.getFilePath(), evidenceData.getFileKey().toString(), evidenceData.getCreatedAt(), evidenceData.getCreator());
    }

    // saveAll 메서드 추가
    public void saveAll(List<EvidenceData> evidenceDataList) {
        String sql = "INSERT INTO Evidence_Data " +
                "(Detail_Item_Code, File_Name, File_Size, File_Path, File_Key, Creator) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, evidenceDataList, evidenceDataList.size(), (ps, evidenceData) -> {
            ps.setString(1, evidenceData.getDetailItemCode());
            ps.setString(2, evidenceData.getFileName());
            ps.setDouble(3, evidenceData.getFileSize());
            ps.setString(4, evidenceData.getFilePath());

            // FileKey를 새로운 UUID로 생성
            String newFileKey = UUID.randomUUID().toString();
            ps.setString(5, newFileKey);

            ps.setString(6, evidenceData.getCreator());
        });
    }

}
