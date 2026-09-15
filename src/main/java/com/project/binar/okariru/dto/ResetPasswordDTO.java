package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class ResetPasswordDTO {

    @Getter
    @Setter
    public static class resetPasswordRequest {

        @NotBlank(message = "Password baru wajib diisi")
        private String newPassword;
    }
}
