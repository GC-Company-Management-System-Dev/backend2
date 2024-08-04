package com.yeogi.scms.service;

import com.yeogi.scms.domain.LoginAccount;
import com.yeogi.scms.repository.LoginAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LoginAccountService implements UserDetailsService {

    private final LoginAccountRepository loginAccountRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public LoginAccountService(LoginAccountRepository loginAccountRepository, PasswordEncoder passwordEncoder) {
        this.loginAccountRepository = loginAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveLoginAccount(String username, String nickname, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        LoginAccount loginAccount = new LoginAccount();
        loginAccount.setUsername(username);
        loginAccount.setNickname(nickname);
        loginAccount.setPassword(hashedPassword);

        loginAccountRepository.save(loginAccount);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginAccount loginAccount = loginAccountRepository.findByUsername(username);
        if (loginAccount == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new User(loginAccount.getUsername(), loginAccount.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public String getNicknameByUsername(String username) {
        LoginAccount loginAccount = loginAccountRepository.findByUsername(username);
        if (loginAccount != null) {
            return loginAccount.getNickname();
        }
        return null;
    }
}
