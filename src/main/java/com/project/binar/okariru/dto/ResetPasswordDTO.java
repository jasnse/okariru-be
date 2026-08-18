package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ResetPasswordDTO {

    @Getter
    @Setter
    public static class resetPasswordRequest {

        @NotNull(message = "Customer ID wajib diisi")
        private Integer customerId;

        @NotBlank(message = "Password baru wajib diisi")
        private String newPassword;
    }
}
