package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.CustomerRequest;
import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<CustomerResponse.getCustomerResponse> getAllCustomer() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> new CustomerResponse.getCustomerResponse(
                        customer.getCustomerId(),
                        customer.getUserName(),
                        customer.getSidName(),
                        customer.getNik(),
                        customer.getTempatLahir(),
                        customer.getTanggalLahir(),
                        customer.getAlamat(),
                        customer.getPekerjaan(),
                        customer.getPendapatan(),
                        customer.getMaritalStatus(),
                        customer.getGender(),
                        customer.getNoRekening(),
                        customer.getCreatedAt(),
                        customer.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public CustomerResponse.getCustomerResponse getCustomerById(Integer id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("customer dengan Id " + id + " tidak ditemukan"));
        return new CustomerResponse.getCustomerResponse(
                customer.getCustomerId(),
                customer.getUserName(),
                customer.getSidName(),
                customer.getNik(),
                customer.getTempatLahir(),
                customer.getTanggalLahir(),
                customer.getAlamat(),
                customer.getPekerjaan(),
                customer.getPendapatan(),
                customer.getMaritalStatus(),
                customer.getGender(),
                customer.getNoRekening(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    @Override
    public CustomerResponse.getCustomerResponse addCustomer(CustomerRequest.customerAddRequest addRequest) {
        if (customerRepository.existsByUserName(addRequest.userName)) {
            throw new IllegalArgumentException("Username " + addRequest.userName + " sudah terdaftar");
        }
        if (customerRepository.existsByEmail(addRequest.email)) {
            throw new IllegalArgumentException("Email " + addRequest.email + " sudah terdaftar");
        }
        if (customerRepository.existsByNik(addRequest.nik)) {
            throw new IllegalArgumentException("NIK " + addRequest.nik + " sudah terdaftar");
        }
        if (addRequest.noRekening != null && customerRepository.existsByNoRekening(addRequest.noRekening)) {
            throw new IllegalArgumentException("No rekening " + addRequest.noRekening + " sudah terdaftar");
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setUserName(addRequest.userName);
        customer.setSidName(addRequest.sidName);
        customer.setEmail(addRequest.email);
        customer.setPassword(passwordEncoder.encode(addRequest.password));
        customer.setNik(addRequest.nik);
        customer.setTempatLahir(addRequest.tempatLahir);
        customer.setTanggalLahir(addRequest.tanggalLahir);
        customer.setAlamat(addRequest.alamat);
        customer.setPekerjaan(addRequest.pekerjaan);
        customer.setPendapatan(addRequest.pendapatan);
        customer.setMaritalStatus(addRequest.maritalStatus);
        customer.setGender(addRequest.gender);
        customer.setNoRekening(addRequest.noRekening);
        customer.setCreatedAt(LocalDate.now());

        CustomerEntity saved = customerRepository.save(customer);
        return new CustomerResponse.getCustomerResponse(
                saved.getCustomerId(),
                saved.getUserName(),
                saved.getSidName(),
                saved.getNik(),
                saved.getTempatLahir(),
                saved.getTanggalLahir(),
                saved.getAlamat(),
                saved.getPekerjaan(),
                saved.getPendapatan(),
                saved.getMaritalStatus(),
                saved.getGender(),
                saved.getNoRekening(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateCustomer(Integer id, String userName, String sidName, String email, String password, String nik,
                                String tempatLahir, LocalDate tanggalLahir, String alamat, String pekerjaan,
                                Integer pendapatan, String maritalStatus, String gender, String noRekening) {
        Optional<CustomerEntity> customerOpt = customerRepository.findById(id);

        if (customerOpt.isEmpty()) {
            throw new EntityNotFoundException("customer id tidak ditemukan");
        }

        if (customerRepository.existsByUserNameAndCustomerIdNot(userName, id)) {
            throw new IllegalArgumentException("Username " + userName + " sudah dipakai customer lain");
        }
        if (customerRepository.existsByEmailAndCustomerIdNot(email, id)) {
            throw new IllegalArgumentException("Email " + email + " sudah dipakai customer lain");
        }
        if (customerRepository.existsByNikAndCustomerIdNot(nik, id)) {
            throw new IllegalArgumentException("NIK " + nik + " sudah dipakai customer lain");
        }
        if (noRekening != null && customerRepository.existsByNoRekeningAndCustomerIdNot(noRekening, id)) {
            throw new IllegalArgumentException("No rekening " + noRekening + " sudah dipakai customer lain");
        }

        CustomerEntity customerUpdate = customerOpt.get();
        customerUpdate.setUserName(userName);
        customerUpdate.setSidName(sidName);
        customerUpdate.setEmail(email);
        customerUpdate.setPassword(passwordEncoder.encode(password));
        customerUpdate.setNik(nik);
        customerUpdate.setTempatLahir(tempatLahir);
        customerUpdate.setTanggalLahir(tanggalLahir);
        customerUpdate.setAlamat(alamat);
        customerUpdate.setPekerjaan(pekerjaan);
        customerUpdate.setPendapatan(pendapatan);
        customerUpdate.setMaritalStatus(maritalStatus);
        customerUpdate.setGender(gender);
        customerUpdate.setNoRekening(noRekening);
        customerUpdate.setUpdatedAt(LocalDate.now());
        customerRepository.save(customerUpdate);
    }

    @Override
    public String deleteCustomer(Integer id) {
        CustomerEntity customerDelete = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("customer id: " + id + " tidak ditemukan"));

        customerRepository.delete(customerDelete);

        return "customer dengan ID: " + id + " Telah di hapus";
    }
}
