package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class AngsuranResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getAngsuranResponse {
        Integer angsuranId;
        Integer transPinjamanId;
        Long jumlahPokok;
        Double jumlahBunga;
        Integer totalAngsuran;
        LocalDate tanggalJatuhTempo;
        String statusAngsuran;
        Integer tenor;
        Integer sisaTagihan;
    }

    @Setter
    @Getter
    public static class angsuranUpdateResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class angsuranBayarResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class angsuranDeleteResponse {
        String Message;
    }
}
