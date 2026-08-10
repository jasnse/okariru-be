package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class pinjamanResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getPinjamanResponse {
        Integer pinjamanId;
        String jenisPinjaman;
        String deskripsiPinjaman;
        Double bunga;
        Double biayaLainnya;
        LocalDate createdAt;
        LocalDate updatedAt;
    }

    @Setter
    @Getter
    public static class pinjamanUpdateResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class pinjamanDeleteResponse {
        String Message;
    }
}
