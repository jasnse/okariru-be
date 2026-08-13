package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequest {

    public static class notificationAddRequest {
        @NotNull(message = "customer id harus di pilih")
        public Integer customerId;
        @NotBlank(message = "isi notifikasi harus diisi")
        public String isiNotifikasi;
    }

    public static class notificationUpdateRequest {
        @NotNull(message = "customer id harus di pilih")
        public Integer customerId;
        @NotBlank(message = "isi notifikasi harus diisi")
        public String isiNotifikasi;
    }
}
