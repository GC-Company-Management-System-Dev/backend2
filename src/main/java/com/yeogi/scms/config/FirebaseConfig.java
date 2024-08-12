package com.yeogi.scms.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

//@Configuration
//public class FirebaseConfig {
//
//    @PostConstruct
//    public void init() {
//        try {
//            FileInputStream serviceAccount = new FileInputStream("src/main/resources/key/firebaseKey.json");
//            FirebaseOptions.Builder optionBuilder = FirebaseOptions.builder();
//            FirebaseOptions options = optionBuilder
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//            FirebaseApp.initializeApp(options);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

@Configuration
@Slf4j
public class FirebaseConfig {
    @Bean
    public FirebaseApp firebaseApp() throws IOException {

        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/key/firebaseKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("scms-1862c.appspot.com") // storage 주소 입력
        .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);

        log.info("FirebaseApp initialized" + app.getName());
        return app;
    }

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        return FirebaseAuth.getInstance(firebaseApp());
    }

    @Bean
    public Bucket bucket() throws IOException {
// Storage Bucket을 Bean으로 등록
        return StorageClient.getInstance(firebaseApp()).bucket();
    }
}
