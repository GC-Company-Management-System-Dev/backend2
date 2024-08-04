package com.yeogi.scms.repository;

import com.yeogi.scms.domain.LoginAccount;
import com.yeogi.scms.util.RowMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LoginAccountRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(LoginAccount loginAccount) {
        String sql = "INSERT INTO Login_Account_Management (Username, Nickname, Password) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, loginAccount.getUsername(), loginAccount.getNickname(), loginAccount.getPassword());
    }

    public LoginAccount findByUsername(String username) {
        String sql = "SELECT * FROM Login_Account_Management WHERE Username = ?";
        List<LoginAccount> accounts = jdbcTemplate.query(sql, new Object[]{username}, new RowMapper<LoginAccount>() {
            @Override
            public LoginAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                return RowMapperUtil.mapLoginAccount(rs);
            }
        });
        return accounts.isEmpty() ? null : accounts.get(0);
    }



}
