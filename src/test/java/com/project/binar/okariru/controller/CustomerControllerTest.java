package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.CustomerRequest;
import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.entity.AppUser;
import com.project.binar.okariru.service.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController controller;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(Object principal) {
        SecurityContextHolder.setContext(
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken(principal, null, List.of())));
    }

    private CustomerResponse.getCustomerResponse customer() {
        return new CustomerResponse.getCustomerResponse(7, "andi", "Andi", "andi@mail.com", "3201", null, null,
                null, null, null, null, null, null, null, null, "CUSTOMER");
    }

    // ---------- CRUD ----------

    @Test
    void findAll() {
        Page<CustomerResponse.getCustomerResponse> page = new PageImpl<>(List.of(customer()));
        when(customerService.findAll("and", 0, 5)).thenReturn(page);

        ResponseEntity<Page<CustomerResponse.getCustomerResponse>> result = controller.findAll("and", 0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void getCustomerById() {
        when(customerService.getCustomerById(7)).thenReturn(customer());

        assertEquals("andi", controller.getCustomerById(7).getBody().getUserName());
    }

    @Test
    void addCustomer() {
        CustomerRequest.customerAddRequest req = new CustomerRequest.customerAddRequest();
        when(customerService.addCustomer(req)).thenReturn(customer());

        assertEquals(7, controller.addCustomer(req).getBody().getCustomerId());
    }

    @Test
    void updateCustomer_meneruskanSemuaField() {
        CustomerRequest.customerUpdateRequest req = new CustomerRequest.customerUpdateRequest();
        req.userName = "andi";
        req.sidName = "Andi";
        req.email = "a@mail.com";
        req.password = "pw";
        req.nik = "3201";
        req.tempatLahir = "Bandung";
        req.tanggalLahir = LocalDate.of(2000, 1, 1);
        req.alamat = "Jl. A";
        req.pekerjaan = "Dev";
        req.pendapatan = 5;
        req.maritalStatus = "Single";
        req.gender = "M";
        req.noRekening = "999";

        ResponseEntity<CustomerResponse.customerUpdateResponse> result = controller.updateCustomer(7, req);

        verify(customerService).updateCustomer(7, "andi", "Andi", "a@mail.com", "pw", "3201", "Bandung",
                LocalDate.of(2000, 1, 1), "Jl. A", "Dev", 5, "Single", "M", "999");
        assertEquals("Customer Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deleteCustomer() {
        ResponseEntity<CustomerResponse.customerDeleteResponse> result = controller.deleteCustomer(7);

        verify(customerService).deleteCustomer(7);
        assertEquals("Delete customer successfully", result.getBody().getMessage());
    }

    // ---------- PUT /me ----------

    @Test
    void updateMyProfile_customerLogin_memakaiUserIdDariPrincipal() {
        loginAs(new AppUser("andi", "pw", "CUSTOMER", 7));
        CustomerRequest.customerSelfUpdateRequest req = new CustomerRequest.customerSelfUpdateRequest();
        req.sidName = "Andi";
        req.alamat = "Jl. A";
        req.pekerjaan = "Dev";
        req.pendapatan = 5;
        req.maritalStatus = "Single";
        req.noRekening = "999";
        req.tempatLahir = "Bandung";
        req.tanggalLahir = LocalDate.of(2000, 1, 1);
        req.gender = "M";

        ResponseEntity<CustomerResponse.customerUpdateResponse> result = controller.updateMyProfile(req);

        verify(customerService).updateOwnProfile(7, "Andi", "Jl. A", "Dev", 5, "Single", "999", "Bandung",
                LocalDate.of(2000, 1, 1), "M");
        assertEquals("Profil berhasil diperbarui", result.getBody().getMessage());
    }

    @Test
    void updateMyProfile_employeeTanpaUserId_forbidden() {
        loginAs(new AppUser("budi", "pw", "ADMIN", null));

        ResponseEntity<CustomerResponse.customerUpdateResponse> result =
                controller.updateMyProfile(new CustomerRequest.customerSelfUpdateRequest());

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        verifyNoInteractions(customerService);
    }

    @Test
    void updateMyProfile_principalBukanAppUser_forbidden() {
        loginAs("anonymousUser");

        ResponseEntity<CustomerResponse.customerUpdateResponse> result =
                controller.updateMyProfile(new CustomerRequest.customerSelfUpdateRequest());

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        verifyNoInteractions(customerService);
    }

    // ---------- PUT /fcm-token ----------

    @Test
    void updateFcmToken_customerLogin() {
        loginAs(new AppUser("andi", "pw", "CUSTOMER", 7));
        CustomerRequest.customerFcmTokenRequest req = new CustomerRequest.customerFcmTokenRequest();
        req.fcmToken = "token-abc";

        ResponseEntity<CustomerResponse.customerUpdateResponse> result = controller.updateFcmToken(req);

        verify(customerService).updateFcmToken(7, "token-abc");
        assertEquals("Token FCM berhasil diperbarui", result.getBody().getMessage());
    }

    @Test
    void updateFcmToken_tanpaUserId_forbidden() {
        loginAs(new AppUser("budi", "pw", "ADMIN", null));

        ResponseEntity<CustomerResponse.customerUpdateResponse> result =
                controller.updateFcmToken(new CustomerRequest.customerFcmTokenRequest());

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        verify(customerService, never()).updateFcmToken(anyInt(), anyString());
    }

    @Test
    void updateFcmToken_principalBukanAppUser_forbidden() {
        loginAs("anonymousUser");

        ResponseEntity<CustomerResponse.customerUpdateResponse> result =
                controller.updateFcmToken(new CustomerRequest.customerFcmTokenRequest());

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    // ---------- DELETE /fcm-token ----------

    @Test
    void clearFcmToken_customerLogin_mengirimNull() {
        loginAs(new AppUser("andi", "pw", "CUSTOMER", 7));

        ResponseEntity<CustomerResponse.customerUpdateResponse> result = controller.clearFcmToken();

        verify(customerService).updateFcmToken(7, null);
        assertEquals("Token FCM berhasil dihapus", result.getBody().getMessage());
    }

    @Test
    void clearFcmToken_tanpaUserId_forbidden() {
        loginAs(new AppUser("budi", "pw", "ADMIN", null));

        assertEquals(HttpStatus.FORBIDDEN, controller.clearFcmToken().getStatusCode());
        verify(customerService, never()).updateFcmToken(any(), any());
    }

    @Test
    void clearFcmToken_principalBukanAppUser_forbidden() {
        loginAs("anonymousUser");

        assertEquals(HttpStatus.FORBIDDEN, controller.clearFcmToken().getStatusCode());
    }
}
