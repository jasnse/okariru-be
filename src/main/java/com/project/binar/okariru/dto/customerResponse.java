package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class CustomerResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getCustomerResponse {
        Integer customerId;
        String userName;
        String sidName;
        String email;
        String nik;
        String tempatLahir;
        LocalDate tanggalLahir;
        String alamat;
        String pekerjaan;
        Integer pendapatan;
        String maritalStatus;
        String gender;
        String noRekening;
        LocalDate createdAt;
        LocalDate updatedAt;
        String roleCustomer;
    }

    @Setter
    @Getter
    public static class customerUpdateResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class customerDeleteResponse {
        String Message;
    }
}
