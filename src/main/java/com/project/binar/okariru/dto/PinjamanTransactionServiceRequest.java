package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PinjamanTransactionServiceRequest {

    public static class pinjamanTransactionAddRequest {
        @NotNull(message = "customer id harus di pilih")
        public Integer customerId;
        public Integer pinjamanId;
        @NotNull(message = "nominal pinjaman harus diisi")
        public Integer nominalPinjaman;
        public Integer tenor;
    }

    public static class pinjamanTransactionUpdateRequest {
        @NotNull(message = "customer id harus di pilih")
        public Integer customerId;
        public Integer pinjamanId;
        public Integer nominalPinjaman;
        public Integer tenor;
        public String statusPengajuan;
        public LocalDate tanggalReview;
        public LocalDate tanggalApproval;
        public String noteMarketing;
        public String noteBm;
        public String noteBackOffice;
        public Integer lastUpdateBy;
    }
}
