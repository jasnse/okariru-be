package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.CustomerRequest;
import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    //get all customer
    @GetMapping
    public ResponseEntity<List<CustomerResponse.getCustomerResponse>> findAll() {
        return ResponseEntity.ok(customerService.getAllCustomer());
    }

    //get customer by Id
    @GetMapping(headers = "idCustomerSearch")
    public ResponseEntity<CustomerResponse.getCustomerResponse> getCustomerById(
            @RequestHeader("idCustomerSearch") Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    //add customer
    @PostMapping
    public ResponseEntity<CustomerResponse.getCustomerResponse> addCustomer(
            @Valid @RequestBody CustomerRequest.customerAddRequest request) {
        return ResponseEntity.ok(customerService.addCustomer(request));
    }

    //update customer
    @PutMapping
    public ResponseEntity<CustomerResponse.customerUpdateResponse> updateCustomer(
            @RequestParam Integer id,
            @Valid @RequestBody CustomerRequest.customerUpdateRequest request
    ) {
        customerService.updateCustomer(id, request.userName, request.sidName, request.email, request.password,
                request.nik, request.tempatLahir, request.tanggalLahir, request.alamat, request.pekerjaan,
                request.pendapatan, request.maritalStatus, request.gender, request.noRekening);

        CustomerResponse.customerUpdateResponse respUpdate = new CustomerResponse.customerUpdateResponse();
        respUpdate.setMessage("Customer Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete customer
    @DeleteMapping
    public ResponseEntity<CustomerResponse.customerDeleteResponse> deleteCustomer(@RequestParam Integer Id) {
        customerService.deleteCustomer(Id);

        CustomerResponse.customerDeleteResponse respDelete = new CustomerResponse.customerDeleteResponse();
        respDelete.setMessage("Delete customer successfully");
        return ResponseEntity.ok(respDelete);
    }
}
