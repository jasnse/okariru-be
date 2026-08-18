package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.CustomerRequest;
import com.project.binar.okariru.dto.CustomerResponse;

import java.time.LocalDate;
import java.util.List;

public interface CustomerService {

    List<CustomerResponse.getCustomerResponse> getAllCustomer();

    CustomerResponse.getCustomerResponse getCustomerById(Integer id);

    CustomerResponse.getCustomerResponse addCustomer(CustomerRequest.customerAddRequest addRequest);

    void updateCustomer(Integer id, String userName, String sidName, String email, String password, String nik,
                         String tempatLahir, LocalDate tanggalLahir, String alamat, String pekerjaan,
                         Integer pendapatan, String maritalStatus, String gender, String noRekening);

    String deleteCustomer(Integer id);
}
