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

    // payload khusus tahap review oleh MARKETING: Pengajuan -> Direview
    public static class pinjamanTransactionReviewRequest {
        public String note;
    }

    // payload khusus tahap approval oleh BRANCH_MANAGER: Direview -> Disetujui/Ditolak
    public static class pinjamanTransactionApprovalRequest {
        @NotNull(message = "keputusan approval (approved) harus diisi")
        public Boolean approved;
        public String note;
    }

    // payload khusus tahap pencairan oleh BACKOFFICE: Disetujui -> Dicairkan
    public static class pinjamanTransactionDisburseRequest {
        public String note;
    }
}
