package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class pinjamanRequest {

    public static class pinjamanAddRequest {
        @NotBlank(message = "jenis pinjaman harus diisi")
        public String jenisPinjaman;
        @NotBlank(message = "deskripsi pinjaman harus diisi")
        public String deskripsiPinjaman;
        @NotNull(message = "bunga harus diisi")
        public Double bunga;
        @NotNull(message = "biaya lainnya harus diisi")
        public Double biayaLainnya;
    }

    public static class pinjamanUpdateRequest {
        @NotBlank(message = "jenis pinjaman harus diisi")
        public String jenisPinjaman;
        @NotBlank(message = "deskripsi pinjaman harus diisi")
        public String deskripsiPinjaman;
        @NotNull(message = "bunga harus diisi")
        public Double bunga;
        @NotNull(message = "biaya lainnya harus diisi")
        public Double biayaLainnya;
    }
}
