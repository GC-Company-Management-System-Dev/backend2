package com.yeogi.scms;

import com.yeogi.scms.domain.SecurityCertificationMaster;
import com.yeogi.scms.repository.SecurityCertificationMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ScmsApplication implements CommandLineRunner {

	@Autowired
	private SecurityCertificationMasterRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ScmsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<SecurityCertificationMaster> masters = repository.findAll();
		for (SecurityCertificationMaster master : masters) {
			System.out.println("Document Code: " + master.getDocumentCode());
			System.out.println("Classification Code: " + master.getClassificationCode());
			System.out.println("Certification Year: " + master.getCertificationYear());
			System.out.println("Item Code: " + master.getItemCode());
			System.out.println("Certification Type Name: " + master.getCertificationTypeName());
			System.out.println("ISO Details: " + master.getIsoDetails());
			System.out.println("PCI DSS Details: " + master.getPciDssDetails());
			System.out.println("Created At: " + master.getCreatedAt());
			System.out.println("Updated At: " + master.getUpdatedAt());
			System.out.println("=====================================");
		}
	}
}
