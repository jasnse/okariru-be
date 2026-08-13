package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class NotificationResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getNotificationResponse {
        Integer notifId;
        Integer customerId;
        String isiNotifikasi;
        LocalDate tanggalNotifikasi;
    }

    @Setter
    @Getter
    public static class notificationUpdateResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class notificationDeleteResponse {
        String Message;
    }
}
