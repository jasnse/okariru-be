package com.project.binar.okariru.service;

public interface PushNotificationService {

    // channel: "general" | "transaction" | "promo" -- cocokkan dengan NotificationChannelType di mobile app
    // deepLink: URI custom scheme app, contoh "okariru://status-pinjaman/123" (boleh null kalau tidak perlu buka halaman spesifik)
    void sendToCustomer(Integer customerId, String title, String body, String channel, String deepLink);
}
