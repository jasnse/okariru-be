package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class MenuRequest {

    public static class menuAddRequest{
        @NotBlank(message = "Nama menu gak boleh kosong")
        public String namaMenu;
        @NotBlank(message = "Minimal tambahin deskripsi menu nya")
        public String deskripsiMenu;
        public String path;
        public String icon;
        public LocalDate createdAt;
    }

    public static class menuUpdateRequest{
        @NotBlank(message = "Nama menu gak boleh kosong")
        public String namaMenu;
        @NotBlank(message = "deskripsi Menu gak boleh kosong")
        public String deskripsiMenu;
        public String path;
        public String icon;
    }
}
