package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.dto.ResetPasswordDTO;
import com.project.binar.okariru.service.CustomerService;
import com.project.binar.okariru.service.jwtAuth.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordControllerTest {

    @Mock
    private OtpService otpService;
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ResetPasswordController controller;

    private CustomerResponse.getCustomerResponse customer() {
        return new CustomerResponse.getCustomerResponse(7, "andi", "Andi", "andi@mail.com", "3201", null, null,
                null, null, null, null, null, null, null, null, "CUSTOMER");
    }

    @Test
    void requestOtp_mengembalikanPesan() {
        when(otpService.generateOtp("andi@mail.com")).thenReturn("12345");

        ResponseEntity<String> result = controller.requestOtp("andi@mail.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("andi@mail.com"));
    }

    @Test
    void validateAndResetToken_otpValid() {
        when(otpService.validateOtpAndGetResetToken("andi@mail.com", "12345")).thenReturn("54321");

        ResponseEntity<?> result = controller.validateAndResetToken("andi@mail.com", "12345");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertEquals("54321", body.get("resetToken"));
        assertNotNull(body.get("message"));
    }

    @Test
    void validateAndResetToken_otpSalah_badRequest() {
        when(otpService.validateOtpAndGetResetToken("andi@mail.com", "00000")).thenReturn(null);

        ResponseEntity<?> result = controller.validateAndResetToken("andi@mail.com", "00000");

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void resetPassword_tokenTidakValid_forbidden() {
        when(otpService.getEmailByResetToken("54321")).thenReturn(null);
        ResetPasswordDTO.resetPasswordRequest req = new ResetPasswordDTO.resetPasswordRequest();
        req.setNewPassword("baru");

        ResponseEntity<String> result = controller.resetPassword("54321", req);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        verifyNoInteractions(customerService);
        verify(otpService, never()).deleteResetToken(anyString());
    }

    @Test
    void resetPassword_berhasil_menghapusResetToken() {
        when(otpService.getEmailByResetToken("54321")).thenReturn("andi@mail.com");
        when(customerService.getCustomerByEmail("andi@mail.com")).thenReturn(customer());
        ResetPasswordDTO.resetPasswordRequest req = new ResetPasswordDTO.resetPasswordRequest();
        req.setNewPassword("baru");

        ResponseEntity<String> result = controller.resetPassword("54321", req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(customerService).resetPasswordCustomer(7, "baru");
        verify(otpService).deleteResetToken("54321");
    }
}
