package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.CustomerRequest;
import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.PlafondEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.PlafondRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PlafondRepository plafondRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl service;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customer = new CustomerEntity();
        customer.setCustomerId(7);
        customer.setUserName("andi");
        customer.setEmail("andi@mail.com");
        customer.setNik("3201");
        customer.setNoRekening("999");
        customer.setRoleCustomer("CUSTOMER");
    }

    private CustomerRequest.customerAddRequest addRequest(String noRekening) {
        CustomerRequest.customerAddRequest req = new CustomerRequest.customerAddRequest();
        req.userName = "andi";
        req.sidName = "Andi S";
        req.email = "andi@mail.com";
        req.password = "rahasia";
        req.nik = "3201";
        req.noRekening = noRekening;
        req.tanggalLahir = LocalDate.of(2000, 1, 1);
        return req;
    }

    // ---------- read ----------

    @Test
    void findAll_memetakanPage() {
        when(customerRepository.searchCustomer(eq("and"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(customer)));

        Page<CustomerResponse.getCustomerResponse> result = service.findAll("and", 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("andi", result.getContent().get(0).getUserName());
    }

    @Test
    void getCustomerById_ditemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        assertEquals("andi@mail.com", service.getCustomerById(7).getEmail());
    }

    @Test
    void getCustomerById_tidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getCustomerById(7));
    }

    @Test
    void getCustomerByEmail_ditemukan() {
        when(customerRepository.findByEmail("andi@mail.com")).thenReturn(Optional.of(customer));

        assertEquals(7, service.getCustomerByEmail("andi@mail.com").getCustomerId());
    }

    @Test
    void getCustomerByEmail_tidakDitemukan() {
        when(customerRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getCustomerByEmail("x@mail.com"));
    }

    // ---------- addCustomer ----------

    @Test
    void addCustomer_berhasil_membuatPlafondDefault() {
        when(passwordEncoder.encode("rahasia")).thenReturn("HASH");
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponse.getCustomerResponse r = service.addCustomer(addRequest("999"));

        assertEquals("CUSTOMER", r.getRoleCustomer());
        assertEquals(LocalDate.now(), r.getCreatedAt());

        ArgumentCaptor<CustomerEntity> custCaptor = ArgumentCaptor.forClass(CustomerEntity.class);
        verify(customerRepository).save(custCaptor.capture());
        assertEquals("HASH", custCaptor.getValue().getPassword());

        ArgumentCaptor<PlafondEntity> plafondCaptor = ArgumentCaptor.forClass(PlafondEntity.class);
        verify(plafondRepository).save(plafondCaptor.capture());
        assertEquals(10_000_000, plafondCaptor.getValue().getTotalPlafond());
        assertEquals(custCaptor.getValue(), plafondCaptor.getValue().getUser());
    }

    @Test
    void addCustomer_tanpaNoRekening_tidakMengecekNoRekening() {
        when(passwordEncoder.encode("rahasia")).thenReturn("HASH");
        when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        service.addCustomer(addRequest(null));

        verify(customerRepository, never()).existsByNoRekening(any());
    }

    @Test
    void addCustomer_usernameSudahAda() {
        when(customerRepository.existsByUserName("andi")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addCustomer(addRequest("999")));
        assertTrue(ex.getMessage().contains("Username"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void addCustomer_emailSudahAda() {
        when(customerRepository.existsByEmail("andi@mail.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addCustomer(addRequest("999")));
        assertTrue(ex.getMessage().contains("Email"));
    }

    @Test
    void addCustomer_nikSudahAda() {
        when(customerRepository.existsByNik("3201")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addCustomer(addRequest("999")));
        assertTrue(ex.getMessage().contains("NIK"));
    }

    @Test
    void addCustomer_noRekeningSudahAda() {
        when(customerRepository.existsByNoRekening("999")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addCustomer(addRequest("999")));
        assertTrue(ex.getMessage().contains("rekening"));
        verify(plafondRepository, never()).save(any());
    }

    // ---------- resetPasswordCustomer ----------

    @Test
    void resetPasswordCustomer_berhasil() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("baru")).thenReturn("HASH-BARU");

        service.resetPasswordCustomer(7, "baru");

        assertEquals("HASH-BARU", customer.getPassword());
        verify(customerRepository).save(customer);
    }

    @Test
    void resetPasswordCustomer_tidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.resetPasswordCustomer(7, "baru"));
    }

    // ---------- updateCustomer ----------

    private void callUpdate(String noRekening) {
        service.updateCustomer(7, "andi2", "Andi Dua", "andi2@mail.com", "pass", "3202",
                "Bandung", LocalDate.of(2001, 2, 2), "Jl. A", "Dev", 5_000_000, "Single", "M", noRekening);
    }

    @Test
    void updateCustomer_berhasil() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("pass")).thenReturn("HASH");

        callUpdate("888");

        assertEquals("andi2", customer.getUserName());
        assertEquals("HASH", customer.getPassword());
        assertEquals("888", customer.getNoRekening());
        assertEquals(LocalDate.now(), customer.getUpdatedAt());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_tanpaNoRekening_tidakMengecekNoRekening() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("pass")).thenReturn("HASH");

        callUpdate(null);

        verify(customerRepository, never()).existsByNoRekeningAndCustomerIdNot(any(), any());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_tidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> callUpdate("888"));
    }

    @Test
    void updateCustomer_usernameDipakaiLain() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByUserNameAndCustomerIdNot("andi2", 7)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> callUpdate("888"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_emailDipakaiLain() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmailAndCustomerIdNot("andi2@mail.com", 7)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> callUpdate("888"));
    }

    @Test
    void updateCustomer_nikDipakaiLain() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByNikAndCustomerIdNot("3202", 7)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> callUpdate("888"));
    }

    @Test
    void updateCustomer_noRekeningDipakaiLain() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByNoRekeningAndCustomerIdNot("888", 7)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> callUpdate("888"));
        verify(customerRepository, never()).save(any());
    }

    // ---------- updateOwnProfile ----------

    private void callUpdateOwn(String noRekening) {
        service.updateOwnProfile(7, "Andi Baru", "Jl. B", "PNS", 6_000_000, "Married", noRekening,
                "Jakarta", LocalDate.of(1999, 3, 3), "M");
    }

    @Test
    void updateOwnProfile_berhasil() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        callUpdateOwn("777");

        assertEquals("Andi Baru", customer.getSidName());
        assertEquals("777", customer.getNoRekening());
        assertEquals(LocalDate.now(), customer.getUpdatedAt());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateOwnProfile_tanpaNoRekening() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        callUpdateOwn(null);

        assertNull(customer.getNoRekening());
        verify(customerRepository, never()).existsByNoRekeningAndCustomerIdNot(any(), any());
    }

    @Test
    void updateOwnProfile_tidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> callUpdateOwn("777"));
    }

    @Test
    void updateOwnProfile_noRekeningDipakaiLain() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByNoRekeningAndCustomerIdNot("777", 7)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> callUpdateOwn("777"));
        verify(customerRepository, never()).save(any());
    }

    // ---------- updateFcmToken ----------

    @Test
    void updateFcmToken_berhasil() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        service.updateFcmToken(7, "token-baru");

        assertEquals("token-baru", customer.getFcmToken());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateFcmToken_null_menghapusToken() {
        customer.setFcmToken("lama");
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        service.updateFcmToken(7, null);

        assertNull(customer.getFcmToken());
    }

    @Test
    void updateFcmToken_tidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateFcmToken(7, "t"));
    }

    // ---------- deleteCustomer ----------

    @Test
    void deleteCustomer_berhasil() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        String msg = service.deleteCustomer(7);

        assertTrue(msg.contains("7"));
        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_tidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteCustomer(7));
    }
}
