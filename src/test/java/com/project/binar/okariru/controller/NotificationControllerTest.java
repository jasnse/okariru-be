package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.NotificationRequest;
import com.project.binar.okariru.dto.NotificationResponse;
import com.project.binar.okariru.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController controller;

    private NotificationResponse.getNotificationResponse notif() {
        return new NotificationResponse.getNotificationResponse(3, 7, "Halo", null);
    }

    @Test
    void findAll() {
        when(notificationService.getAllNotification()).thenReturn(List.of(notif()));

        ResponseEntity<List<NotificationResponse.getNotificationResponse>> result = controller.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getNotificationByCustID() {
        when(notificationService.getNotificationByCustomerId(7)).thenReturn(List.of(notif()));

        assertEquals(1, controller.getNotificationByCustID(7).getBody().size());
    }

    @Test
    void addNotification() {
        NotificationRequest.notificationAddRequest req = new NotificationRequest.notificationAddRequest();
        when(notificationService.addNotification(req)).thenReturn(notif());

        assertEquals(3, controller.addNotification(req).getBody().getNotifId());
    }

    @Test
    void updateNotification_memanggilServiceDanMengembalikanPesan() {
        NotificationRequest.notificationUpdateRequest req = new NotificationRequest.notificationUpdateRequest();
        req.customerId = 7;
        req.isiNotifikasi = "Baru";

        ResponseEntity<NotificationResponse.notificationUpdateResponse> result = controller.updateNotification(3, req);

        verify(notificationService).updateNotification(3, 7, "Baru");
        assertEquals("Notification Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deleteNotification_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<NotificationResponse.notificationDeleteResponse> result = controller.deleteNotification(3);

        verify(notificationService).deleteNotification(3);
        assertEquals("Delete notification successfully", result.getBody().getMessage());
    }
}
