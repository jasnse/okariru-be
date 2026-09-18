package com.project.binar.okariru.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

// Inisialisasi Firebase Admin SDK sekali di awal, dipakai PushNotificationService buat kirim FCM.
// Kalau file credential belum ada (misal belum di-setup di environment ini), servis lain tetap
// jalan normal -- cuma kirim notifikasi yang bakal di-skip (lihat PushNotificationServiceImpl).
@Slf4j
@Component
public class FirebaseConfig {

    @Value("${firebase.credentials.path:firebase-service-account.json}")
    private String credentialsPath;

    @PostConstruct
    public void init() {
        Path path = Path.of(credentialsPath);
        if (!Files.exists(path)) {
            log.warn("Firebase service account tidak ditemukan di '{}'. Push notification FCM dinonaktifkan " +
                    "sampai file credential-nya disediakan.", credentialsPath);
            return;
        }

        try (InputStream serviceAccount = new FileInputStream(path.toFile())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK berhasil diinisialisasi dari '{}'.", credentialsPath);
            }
        } catch (IOException e) {
            log.error("Gagal inisialisasi Firebase Admin SDK dari '{}': {}", credentialsPath, e.getMessage());
        }
    }
}
