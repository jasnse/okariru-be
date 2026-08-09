package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class roleRequest {

    public static class roleAddRequest{
        @NotBlank(message = "Nama Role gak boleh kosong")
        public String namaRole;
        public LocalDate createdAt;
    }

    public static class roleUpdateRequest{
        @NotBlank(message = "Nama Role gak boleh kosong")
        public String nama_role;
    }

}
