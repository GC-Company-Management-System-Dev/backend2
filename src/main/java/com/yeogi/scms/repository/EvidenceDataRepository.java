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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class EvidenceDataRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EvidenceDataRepository.class);

    public EvidenceDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // detailItemCode에 해당하는 증적자료 파일 목록 조회
    public List<EvidenceData> findByDetailCode(String detailItemCode) {
        String sql = "SELECT * FROM Evidence_Data WHERE Detail_Item_Code = ? AND File_Name IS NOT NULL";

        System.out.println("Detail Item Code: " + detailItemCode);

        List<EvidenceData> evidenceDataList = jdbcTemplate.query(sql, new Object[]{detailItemCode}, new RowMapper<EvidenceData>() {
            @Override
            public EvidenceData mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapEvidenceData(rs);
            }
        });

        // 조회된 결과 로그 추가
        logger.info("Query result size: {}", evidenceDataList.size());
        evidenceDataList.forEach(evidenceData ->
                logger.info("Retrieved EvidenceData: DetailItemCode={}, FileName={}",
                        evidenceData.getDetailItemCode(),
                        evidenceData.getFileName()));

        return evidenceDataList;
    }

    // 파일 정보 저장
    public void saveEvidenceData(EvidenceData evidenceData) {
        // 중복 데이터 삭제
        String deleteSql = "DELETE FROM Evidence_Data WHERE Detail_Item_Code = ? AND File_Name = ?";
        jdbcTemplate.update(deleteSql, evidenceData.getDetailItemCode(), evidenceData.getFileName());

        // 새로운 데이터 삽입
        String sql = "INSERT INTO Evidence_Data (Detail_Item_Code, File_Name, File_Size, File_Path, File_Key, Created_At, Creator, Year) " +
                "VALUES (?, ?, ?, ?, ?, CONVERT_TZ(NOW(), 'UTC', 'Asia/Seoul'), ?, ?)";

        jdbcTemplate.update(sql, evidenceData.getDetailItemCode(), evidenceData.getFileName(),
                evidenceData.getFileSize(), evidenceData.getFilePath(),
                evidenceData.getFileKey().toString(), evidenceData.getCreator(), evidenceData.getYear());
    }

    // detailItemCode에 해당하는 가장 최근 수정 일시와 변경자 조회
    public Map<String, Object> findLatestModificationInfoByDetailItemCode(String detailItemCode) {
        String sql = "SELECT MAX(Created_At) AS lastModified, Creator FROM Evidence_Data WHERE Detail_Item_Code = ? GROUP BY Creator ORDER BY lastModified DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, new Object[]{detailItemCode}, (rs, rowNum) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("lastModified", rs.getTimestamp("lastModified").toLocalDateTime());
            result.put("creator", rs.getString("Creator"));
            return result;
        });
    }


    // 파일 정보 삭제
    public void deleteByDetailItemCodeAndFileName(String detailItemCode, String fileName) {
        String sql = "DELETE FROM Evidence_Data WHERE Detail_Item_Code = ? AND File_Name = ?";

        // Debugging statements
        System.out.println("rdic: " + detailItemCode);
        System.out.println("rfn: " + fileName);

        int rowsAffected = jdbcTemplate.update(sql, detailItemCode, fileName);
        if (rowsAffected > 0) {
            logger.info("Successfully deleted file information from DB.");
        } else {
            logger.warn("No matching file information found in DB.");
        }
    }

    // 파일 이름이 없거나 파일 크기가 0인 파일 정보 삭제
    public void deleteEmptyFiles(String detailItemCode) {
        String sql = "DELETE FROM Evidence_Data WHERE Detail_Item_Code = ? AND (File_Name IS NULL OR File_Name = '' OR File_Size = 0)";
        jdbcTemplate.update(sql, detailItemCode);
    }


    // saveAll 메서드 추가
    public void saveAll(List<EvidenceData> evidenceDataList) {
        String sql = "INSERT INTO Evidence_Data " +
                "(Detail_Item_Code, File_Name, File_Size, File_Path, File_Key, Creator, Year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, evidenceDataList, evidenceDataList.size(), (ps, evidenceData) -> {
            ps.setString(1, evidenceData.getDetailItemCode());
            ps.setString(2, evidenceData.getFileName());
            ps.setDouble(3, evidenceData.getFileSize());
            ps.setString(4, evidenceData.getFilePath());

            // FileKey를 새로운 UUID로 생성
            String newFileKey = UUID.randomUUID().toString();
            ps.setString(5, newFileKey);

            ps.setString(6, evidenceData.getCreator());
            ps.setInt(7, evidenceData.getYear());
        });
    }
}
