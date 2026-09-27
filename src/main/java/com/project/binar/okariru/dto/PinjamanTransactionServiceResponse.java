package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class PinjamanTransactionServiceResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getPinjamanTransactionResponse {
        Integer transPinjamanId;
        String kodeTransaksi;
        Integer customerId;
        String customerName;
        Integer pinjamanId;
        LocalDate tanggalPengajuan;
        LocalDate tanggalReview;
        LocalDate tanggalApproval;
        Integer nominalPinjaman;
        Integer tenor;
        String statusPengajuan;
        String noteMarketing;
        String noteBm;
        String noteBackOffice;
        LocalDate lastUpdate;
        Integer lastUpdateBy;
        String jenisPinjaman;

        // snapshot: data customer & pinjaman PADA SAAT pengajuan dibuat, tidak ikut berubah
        // walau profil customer di-update atau master data pinjaman diubah admin setelahnya
        String customerNik;
        String customerTempatLahir;
        LocalDate customerTanggalLahir;
        String customerGender;
        String customerAlamat;
        String customerPekerjaan;
        Integer customerPendapatan;
        String customerMaritalStatus;
        String customerNoRekening;
        String deskripsiPinjaman;
        Double bunga;
        Double biayaLainnya;
    }

    @Setter
    @Getter
    public static class pinjamanTransactionUpdateResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class pinjamanTransactionDeleteResponse {
        String Message;
    }
}
