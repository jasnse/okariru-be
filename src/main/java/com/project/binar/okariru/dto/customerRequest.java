package com.project.binar.okariru.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CustomerRequest {

    public static class customerAddRequest {
        @NotBlank(message = "Username harus diisi")
        @Size(max = 100, message = "Username maksimal 100 karakter")
        public String userName;

        @Size(max = 100, message = "Nama maksimal 100 karakter")
        public String sidName;

        @NotBlank(message = "Email harus diisi")
        @Email(message = "Format email tidak valid")
        @Size(max = 100, message = "Email maksimal 100 karakter")
        public String email;

        @NotBlank(message = "Password harus diisi")
        @Size(max = 72, message = "Password maximal 72 karakter")
        public String password;

        @NotBlank(message = "NIK harus diisi")
        @Pattern(regexp = "\\d{16}", message = "NIK harus 16 digit angka")
        public String nik;

        @Size(max = 50, message = "Tempat lahir maksimal 50 karakter")
        public String tempatLahir;
        @Size(max = 50, message = "Alamat maksimal 50 karakter")
        public String alamat;
        @Size(max = 50, message = "Pekerjaan maksimal 50 karakter")
        public String pekerjaan;
        @Size(max = 20, message = "Status pernikahan maksimal 20 karakter")
        public String maritalStatus;
        @Size(max = 20, message = "Gender maksimal 20 karakter")
        public String gender;

        @Pattern(regexp = "\\d{10}", message = "No rekening harus 10 digit angka")
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

    // dikirim mobile app tiap dapat token FCM baru (PUT /api/v1/customer/fcm-token)
    public static class customerFcmTokenRequest {
        @NotBlank(message = "fcmToken harus diisi")
        public String fcmToken;
    }
}
