package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CustomerRequest {

    public static class customerAddRequest {
        @NotBlank(message = "username harus diisi")
        public String userName;
        public String sidName;
        @NotBlank(message = "email harus diisi")
        public String email;
        @NotBlank(message = "password harus diisi")
        public String password;
        @NotBlank(message = "nik harus diisi")
        public String nik;
        public String tempatLahir;
        public LocalDate tanggalLahir;
        public String alamat;
        public String pekerjaan;
        public Integer pendapatan;
        public String maritalStatus;
        public String gender;
        public String noRekening;
    }

    public static class customerUpdateRequest {
        @NotBlank(message = "username harus diisi")
        public String userName;
        public String sidName;
        @NotBlank(message = "email harus diisi")
        public String email;
        @NotBlank(message = "password harus diisi")
        public String password;
        @NotBlank(message = "nik harus diisi")
        public String nik;
        public String tempatLahir;
        public LocalDate tanggalLahir;
        public String alamat;
        public String pekerjaan;
        public Integer pendapatan;
        public String maritalStatus;
        public String gender;
        public String noRekening;
    }

    // customer buat edit profil sendiri (PUT /api/v1/customer/me) --
    public static class customerSelfUpdateRequest {
        public String sidName;
        public String alamat;
        public String pekerjaan;
        public Integer pendapatan;
        public String maritalStatus;
        public String noRekening;
        public String tempatLahir;
        public LocalDate tanggalLahir;
        public String gender;
    }
}
