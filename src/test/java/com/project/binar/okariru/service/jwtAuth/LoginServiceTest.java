package com.project.binar.okariru.service.jwtAuth;

import com.project.binar.okariru.dto.LoginDTO;
import com.project.binar.okariru.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AppUserDetailsService appUserDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService service;

    private LoginDTO.loginRequest request;

    @BeforeEach
    void setUp() {
        request = new LoginDTO.loginRequest();
        request.setUsername("budi");
        request.setPassword("rahasia");
    }

    // ---------- loginEmploye ----------

    @Test
    void loginEmploye_berhasil() {
        AppUser user = new AppUser("budi", "HASH", "ADMIN", null);
        when(appUserDetailsService.findKaryawan("budi")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rahasia", "HASH")).thenReturn(true);
        when(jwtService.issue(eq(user), any(Instant.class))).thenReturn("jwt-token");

        LoginDTO.loginResponse r = service.loginEmploye(request);

        assertEquals("budi", r.getUsername());
        assertEquals("ADMIN", r.getRole());
        assertEquals("jwt-token", r.getToken());
        assertNull(r.getUserId());
    }

    @Test
    void loginEmploye_usernameTidakDitemukan() {
        when(appUserDetailsService.findKaryawan("budi")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> service.loginEmploye(request));
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginEmploye_passwordSalah() {
        AppUser user = new AppUser("budi", "HASH", "ADMIN", null);
        when(appUserDetailsService.findKaryawan("budi")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rahasia", "HASH")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> service.loginEmploye(request));
        verifyNoInteractions(jwtService);
    }

    // ---------- loginCustomer ----------

    @Test
    void loginCustomer_berhasil() {
        AppUser customer = new AppUser("andi", "HASH", "CUSTOMER", 7);
        request.setUsername("andi");
        when(appUserDetailsService.findCustomer("andi")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("rahasia", "HASH")).thenReturn(true);
        when(jwtService.issue(eq(customer), any(Instant.class))).thenReturn("jwt-cust");

        LoginDTO.loginResponse r = service.loginCustomer(request);

        assertEquals("andi", r.getUsername());
        assertEquals("CUSTOMER", r.getRole());
        assertEquals("jwt-cust", r.getToken());
        assertEquals(7, r.getUserId());
    }

    @Test
    void loginCustomer_usernameTidakDitemukan() {
        when(appUserDetailsService.findCustomer("budi")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> service.loginCustomer(request));
    }

    @Test
    void loginCustomer_passwordSalah() {
        AppUser customer = new AppUser("budi", "HASH", "CUSTOMER", 7);
        when(appUserDetailsService.findCustomer("budi")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("rahasia", "HASH")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> service.loginCustomer(request));
        verifyNoInteractions(jwtService);
    }

    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
