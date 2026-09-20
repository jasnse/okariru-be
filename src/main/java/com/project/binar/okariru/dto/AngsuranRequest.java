package com.project.binar.okariru.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AngsuranRequest {

    public static class angsuranAddRequest {
        @NotNull(message = "trans pinjaman id harus di pilih")
        public Integer transPinjamanId;
        @NotNull(message = "jumlah pokok harus diisi")
        public Long jumlahPokok;
        @NotNull(message = "jumlah bunga harus diisi")
        public Double jumlahBunga;
        @NotNull(message = "total angsuran harus diisi")
        public Integer totalAngsuran;
        @NotNull(message = "tanggal jatuh tempo harus diisi")
        public LocalDate tanggalJatuhTempo;
        public String statusAngsuran;
        @NotNull(message = "tenor harus diisi")
        public Integer tenor;
    }

    public static class angsuranUpdateRequest {
        @NotNull(message = "trans pinjaman id harus di pilih")
        public Integer transPinjamanId;
        @NotNull(message = "jumlah pokok harus diisi")
        public Long jumlahPokok;
        @NotNull(message = "jumlah bunga harus diisi")
        public Double jumlahBunga;
        @NotNull(message = "total angsuran harus diisi")
        public Integer totalAngsuran;
        @NotNull(message = "tanggal jatuh tempo harus diisi")
        public LocalDate tanggalJatuhTempo;
        public String statusAngsuran;
        @NotNull(message = "tenor harus diisi")
        public Integer tenor;
    }

    public static class angsuranGenerateRequest {
        @NotNull(message = "trans pinjaman id harus di pilih")
        public Integer transPinjamanId;
        @Min(value = 1, message = "tenor harus diisi")
        public Integer tenor;
    }

    public static class angsuranBayarRequest {
        @NotNull(message = "trans pinjaman id harus di pilih")
        public Integer transPinjamanId;
        @NotNull(message = "nominal bayar harus diisi")
        public Integer nominalBayar;
    }
}
