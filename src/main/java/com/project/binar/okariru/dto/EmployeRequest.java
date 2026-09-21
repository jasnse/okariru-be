package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class EmployeRequest {

    public static class employeAddRequest{
        @NotBlank(message = "username harus terisi")
         public String username;
        @NotBlank(message = "email harus terisi")
         public String email;
        @NotBlank(message = "password harus terisi")
         public String password;
        @NotBlank(message = "nip harus terisi")
         public String nip;
         public LocalDate joinedDate;
    }

    public static class employeChangeCredentialRequest{
        @NotBlank(message = "email harus terisi")
         public String email;
        @NotBlank(message = "password harus terisi")
         public String password;
        public LocalDate updatedDate;
    }
}
