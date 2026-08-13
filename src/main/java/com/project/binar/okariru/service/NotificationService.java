package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.NotificationRequest;
import com.project.binar.okariru.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse.getNotificationResponse> getAllNotification();

    List<NotificationResponse.getNotificationResponse> getNotificationByCustomerId(Integer customerId);

    NotificationResponse.getNotificationResponse addNotification(NotificationRequest.notificationAddRequest addRequest);

    void updateNotification(Integer id, Integer customerId, String isiNotifikasi);

    String deleteNotification(Integer id);
}
