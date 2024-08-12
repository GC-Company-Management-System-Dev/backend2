package com.yeogi.scms.repository;

import com.yeogi.scms.domain.EvidenceData;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // 파일 정보 저장
    public void saveEvidenceData(EvidenceData evidenceData) {
        String sql = "INSERT INTO Evidence_Data (Detail_Item_Code, File_Name, File_Size, File_Path, File_Key, Created_At, Creator) " +
                "VALUES (?, ?, ?, ?, ?, CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul'), ?)";

        jdbcTemplate.update(sql, evidenceData.getDetailItemCode(), evidenceData.getFileName(),
                evidenceData.getFileSize(), evidenceData.getFilePath(),
                evidenceData.getFileKey().toString(), evidenceData.getCreator());
    }


    public void save(String detailItemCode, Double fileName, String fileSize, String filePath, String creator, String fileKey){
        // 현재 로그인한 사용자의 닉네임을 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        creator = authentication.getName(); // UserDetails의 getUsername()을 통해 가져온다

        String sql = "INSERT Evidence_Data SET File_Name = ?, File_Size = ?, File_Path = ?, File_Key = ?, Created_At = CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul'), Creator = ? WHERE Detail_Item_Code = ?";

        jdbcTemplate.update(sql, fileName, fileSize, filePath, fileKey, creator, detailItemCode);
    }

    public void deleteByFileKey(UUID fileKey) {
        String sql = "DELETE FROM Evidence_Data WHERE File_Key = ?";
        jdbcTemplate.update(sql, fileKey.toString());
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

//    public void saveEvidenceData(EvidenceData evidenceData) {
//        String sql = "INSERT INTO Evidence_Data (Detail_Item_Code, File_Name, File_Size, File_Path, File_Key, Creator) " +
//                "VALUES (?, ?, ?, ?, ?, ?)";
//        jdbcTemplate.update(sql,
//                evidenceData.getDetailItemCode(),
//                evidenceData.getFileName(),
//                evidenceData.getFileSize(),
//                evidenceData.getFilePath(),
//                evidenceData.getFileKey().toString(),
//                evidenceData.getCreator());
//    }

//    public void save(EvidenceData evidenceData) {
//        String sql = "INSERT INTO Evidence_Data (Detail_Item_Code, File_Name, File_Size, File_Path, File_Key, Created_At, Creator) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        jdbcTemplate.update(sql, evidenceData.getDetailItemCode(), evidenceData.getFileName(), evidenceData.getFileSize(),
//                evidenceData.getFilePath(), evidenceData.getFileKey().toString(), evidenceData.getCreatedAt(), evidenceData.getCreator());
//    }
