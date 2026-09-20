package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.LoginDTO;
import com.project.binar.okariru.service.jwtAuth.LoginService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController controller;

    @Test
    void loginKaryawan_berhasil() {
        LoginDTO.loginRequest req = new LoginDTO.loginRequest();
        LoginDTO.loginResponse resp = new LoginDTO.loginResponse("budi", "ADMIN", "jwt", null);
        when(loginService.loginEmploye(req)).thenReturn(resp);

        ResponseEntity<LoginDTO.loginResponse> result = controller.loginKaryawan(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("jwt", result.getBody().getToken());
    }

    @Test
    void loginKaryawan_gagal_exceptionDiteruskan() {
        LoginDTO.loginRequest req = new LoginDTO.loginRequest();
        when(loginService.loginEmploye(req)).thenThrow(new BadCredentialsException("salah"));

        assertThrows(BadCredentialsException.class, () -> controller.loginKaryawan(req));
    }

    @Test
    void loginCustomer_berhasil() {
        LoginDTO.loginRequest req = new LoginDTO.loginRequest();
        LoginDTO.loginResponse resp = new LoginDTO.loginResponse("andi", "CUSTOMER", "jwt-c", 7);
        when(loginService.loginCustomer(req)).thenReturn(resp);

        ResponseEntity<LoginDTO.loginResponse> result = controller.loginCustomer(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(7, result.getBody().getUserId());
    }
}
