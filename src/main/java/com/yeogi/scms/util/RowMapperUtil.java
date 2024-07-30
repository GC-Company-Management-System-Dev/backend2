package com.yeogi.scms.util;

import com.yeogi.scms.domain.BaseEntity;
import com.yeogi.scms.domain.CertifDetail;
import com.yeogi.scms.domain.SCMaster;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperUtil {

    public static void setBaseEntityFields(ResultSet rs, BaseEntity entity) throws SQLException {
        entity.setDocumentCode(rs.getString("Document_Code"));
        entity.setClassificationCode(rs.getString("Classification_Code"));
        entity.setCertificationYear(rs.getInt("Certification_Year"));
        entity.setItemCode(rs.getString("Item_Code"));
        entity.setCreatedAt(rs.getTimestamp("Created_At").toLocalDateTime());
        entity.setUpdatedAt(rs.getTimestamp("Updated_At") != null ? rs.getTimestamp("Updated_At").toLocalDateTime() : null);
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
}
