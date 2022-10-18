package com.transtour.backend.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableEncryptableProperties
@EnableJpaRepositories(basePackages = "com.transtour.backend.notification.repository")
public class NotificationApplication implements CommandLineRunner {

    @Value("${token-header}")
    String token;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(NotificationApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("aplicacion iniciacda");
        System.out.println("token header value " + token);

    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("private-key-fireStore.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
        return FirebaseMessaging.getInstance(app);
    }
}
