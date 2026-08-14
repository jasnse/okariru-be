package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class LoginDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class loginResponse {
        private String username;
        private String token;
    }

    @Getter
    @Setter
    public static class loginRequest {

        @NotBlank(message = "Email wajib diisi")
        private String username;

        @NotBlank(message = "password wajib diisi")
        private String password;

    }
}
