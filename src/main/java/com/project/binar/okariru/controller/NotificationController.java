package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.NotificationRequest;
import com.project.binar.okariru.dto.NotificationResponse;
import com.project.binar.okariru.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    //get all notification
    @GetMapping
    public ResponseEntity<List<NotificationResponse.getNotificationResponse>> findAll() {
        return ResponseEntity.ok(notificationService.getAllNotification());
    }

    //get notification by Id
    @GetMapping(headers = "idNotificationSearch")
    public ResponseEntity<List<NotificationResponse.getNotificationResponse>> getNotificationByCustID(
            @RequestHeader("idNotificationSearch") Integer id) {
        return ResponseEntity.ok(notificationService.getNotificationByCustomerId(id));
    }

    //add notification
    @PostMapping
    public ResponseEntity<NotificationResponse.getNotificationResponse> addNotification(
            @Valid @RequestBody NotificationRequest.notificationAddRequest request) {
        return ResponseEntity.ok(notificationService.addNotification(request));
    }

    //update notification
    @PutMapping
    public ResponseEntity<NotificationResponse.notificationUpdateResponse> updateNotification(
            @RequestParam Integer id,
            @Valid @RequestBody NotificationRequest.notificationUpdateRequest request
    ) {
        notificationService.updateNotification(id, request.customerId, request.isiNotifikasi);

        NotificationResponse.notificationUpdateResponse respUpdate = new NotificationResponse.notificationUpdateResponse();
        respUpdate.setMessage("Notification Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete notification
    @DeleteMapping
    public ResponseEntity<NotificationResponse.notificationDeleteResponse> deleteNotification(@RequestParam Integer Id) {
        notificationService.deleteNotification(Id);

        NotificationResponse.notificationDeleteResponse respDelete = new NotificationResponse.notificationDeleteResponse();
        respDelete.setMessage("Delete notification successfully");
        return ResponseEntity.ok(respDelete);
    }
}
