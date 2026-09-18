package com.project.binar.okariru.service.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {

    private final CustomerRepository customerRepository;

    @Override
    public void sendToCustomer(Integer customerId, String title, String body, String channel, String deepLink) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Firebase belum terinisialisasi, notifikasi ke customer {} dilewati.", customerId);
            return;
        }

        CustomerEntity customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null || customer.getFcmToken() == null || customer.getFcmToken().isBlank()) {
            log.warn("Customer {} tidak punya token FCM aktif, notifikasi dilewati.", customerId);
            return;
        }
        //save prefference
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        data.put("channel", channel);
        if (deepLink != null) {
            data.put("deeplink", deepLink);
        }

        Message message = Message.builder()
                .setFid(customer.getFcmToken())
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                .build();

        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("Push notification terkirim ke customer {} (fid={}): {}", customerId, customer.getFcmToken(), messageId);
        } catch (FirebaseMessagingException e) {
            log.error("Gagal kirim push notification ke customer {} (fid={}): {} [{}]",
                    customerId, customer.getFcmToken(), e.getMessage(), e.getMessagingErrorCode());
        }
    }
}
