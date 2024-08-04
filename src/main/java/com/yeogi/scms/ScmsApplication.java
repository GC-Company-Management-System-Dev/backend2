package com.yeogi.scms;
import com.yeogi.scms.service.LoginAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ScmsApplication{
// implements CommandLineRunner
//	@Autowired
//	private LoginAccountService loginAccountService;

	public static void main(String[] args) {
		SpringApplication.run(ScmsApplication.class, args);
		System.out.println("Application started successfully"); //로그
	}
//
//	@Override
//	public void run(String... args) throws Exception {
//		// 초기화 코드에서 계정 추가
//		loginAccountService.saveLoginAccount("test2", "hyorim", "12345");
//		System.out.println("Initial user account has been created.");
//	}
}

