package com.yeogi.scms.util;

import com.yeogi.scms.domain.*;


import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperUtil {

    public static void setBaseEntityFields(ResultSet rs, BaseEntity entity) throws SQLException {
        entity.setDocumentCode(rs.getString("Document_Code"));
        entity.setClassificationCode(rs.getString("Classification_Code"));
        entity.setCertificationYear(rs.getInt("Certification_Year"));
        entity.setItemCode(rs.getString("Item_Code"));
        entity.setCreatedAt(rs.getTimestamp("Created_At"));
        entity.setUpdatedAt(rs.getTimestamp("Updated_At") != null ? rs.getTimestamp("Updated_At") : null);
    }

    public static void setDetailEntityFields(ResultSet rs, DetailEntity entity) throws SQLException {
        entity.setSequence(rs.getLong("Sequence"));
        entity.setDetailItemCode(rs.getString("Detail_Item_Code"));
        entity.setCreatedAt(rs.getTimestamp("Created_At"));
        entity.setUpdatedAt(rs.getTimestamp("Updated_At") != null ? rs.getTimestamp("Updated_At") : null);
        entity.setModifier(rs.getString("Modifier"));
    }

    public static CertifDetail mapCertifDetail(ResultSet rs) throws SQLException {
        CertifDetail detail = new CertifDetail();
        setBaseEntityFields(rs, detail);
        detail.setDetailItemCode(rs.getString("Detail_Item_Code"));
        detail.setDetailItemTypeName(rs.getString("Detail_Item_Type_Name"));
        detail.setCompleted(rs.getBoolean("Completed"));
        detail.setMonthlyIndex(rs.getInt("Monthly_Index"));
        return detail;
    }

    public static SCMaster mapSCMaster(ResultSet rs) throws SQLException {
        SCMaster master = new SCMaster();
        setBaseEntityFields(rs, master);
        master.setCertificationTypeName(rs.getString("Certification_Type_Name"));
        master.setIsoDetails(rs.getString("ISO_Details"));
        master.setPciDssDetails(rs.getString("PCI_DSS_Details"));
        return master;
    }

    public static CertifContent mapCertifContent(ResultSet rs) throws SQLException {
        CertifContent content = new CertifContent();
        setDetailEntityFields(rs, content);
        content.setCertificationCriteria(rs.getString("Certification_Criteria"));
        content.setKeyCheckpoints(rs.getString("Key_Checkpoints"));
        content.setRelevantLaws(rs.getString("Relevant_Laws"));
        return content;
    }

    public static DefectManage mapDefectManage(ResultSet rs) throws SQLException {
        DefectManage defect = new DefectManage();
        setDetailEntityFields(rs, defect);
        defect.setIsmsP(rs.getString("ISMS_P"));
        defect.setIso27k(rs.getString("ISO27K"));
        defect.setPciDss(rs.getString("PCI_DSS"));
        return defect;
    }

    public static OperationalStatus mapOperationalStatus(ResultSet rs) throws SQLException {
        OperationalStatus status = new OperationalStatus();
        setDetailEntityFields(rs, status);
        status.setStatus(rs.getString("Status"));
        status.setRelatedDocument(rs.getString("Related_Document"));
        status.setEvidenceName(rs.getString("Evidence_Name"));
        return status;
    }

    public static EvidenceData mapEvidenceData(ResultSet rs) throws SQLException {
        EvidenceData evidenceData = new EvidenceData();

        // File_Key 값을 String으로 가져와서 UUID로 변환
        String fileKeyString = rs.getString("File_Key");
        java.util.UUID fileKey = java.util.UUID.fromString(fileKeyString);
        evidenceData.setFileKey(fileKey);

        evidenceData.setDetailItemCode(rs.getString("Detail_Item_Code"));
        evidenceData.setFileName(rs.getString("File_Name"));
        evidenceData.setFileSize(rs.getDouble("File_Size"));
        evidenceData.setFilePath(rs.getString("File_Path"));
        evidenceData.setCreatedAt(rs.getTimestamp("Created_At"));
        evidenceData.setCreator(rs.getString("Creator"));
        evidenceData.setYear(rs.getInt("Year"));

        return evidenceData;
    }

    public static LoginAccount mapLoginAccount(ResultSet rs) throws SQLException {
        LoginAccount loginAccount = new LoginAccount();
        loginAccount.setUsername(rs.getString("Username"));
        loginAccount.setNickname(rs.getString("Nickname"));
        loginAccount.setPassword(rs.getString("Password"));
        return loginAccount;
        }

    public static AccessLog mapAccessLog(ResultSet rs) throws SQLException {
    AccessLog accessLog = new AccessLog();
    accessLog.setLogSequence(rs.getLong("Log_Sequence"));
    accessLog.setAccessId(rs.getString("Access_ID"));
    accessLog.setAction(rs.getString("Action"));
    accessLog.setAccessPath(rs.getString("Access_Path"));
    accessLog.setTimestamp(rs.getTimestamp("Timestamp"));
    return accessLog;}

    public static MonthlyIndexInfo mapMonthlyIndexInfo(ResultSet rs) throws SQLException {
        MonthlyIndexInfo monthlyIndexInfo = new MonthlyIndexInfo();
        monthlyIndexInfo.setCertificationYear(rs.getInt("Certification_Year"));
        monthlyIndexInfo.setJan(rs.getInt("Jan"));
        monthlyIndexInfo.setFeb(rs.getInt("Feb"));
        monthlyIndexInfo.setMar(rs.getInt("Mar"));
        monthlyIndexInfo.setApr(rs.getInt("Apr"));
        monthlyIndexInfo.setMay(rs.getInt("May"));
        monthlyIndexInfo.setJun(rs.getInt("Jun"));
        monthlyIndexInfo.setJul(rs.getInt("Jul"));
        monthlyIndexInfo.setAug(rs.getInt("Aug"));
        monthlyIndexInfo.setSep(rs.getInt("Sep"));
        monthlyIndexInfo.setOct(rs.getInt("Oct"));
        monthlyIndexInfo.setNov(rs.getInt("Nov"));
        monthlyIndexInfo.setDecem(rs.getInt("Decem"));
        monthlyIndexInfo.setCreatedAt(rs.getTimestamp("Created_At"));
        return monthlyIndexInfo;
    }

    }

