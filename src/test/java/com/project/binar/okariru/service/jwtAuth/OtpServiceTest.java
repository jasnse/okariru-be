package com.project.binar.okariru.service.jwtAuth;

import com.project.binar.okariru.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private EmailService emailService;
    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private OtpService service;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    // ---------- generateOtp ----------

    @Test
    void generateOtp_emailTidakTerdaftar() {
        when(customerRepository.existsByEmail("x@mail.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.generateOtp("x@mail.com"));
        verifyNoInteractions(emailService);
        verify(valueOps, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void generateOtp_berhasil_menyimpanKeRedisDanMengirimEmail() {
        when(customerRepository.existsByEmail("andi@mail.com")).thenReturn(true);

        String otp = service.generateOtp("andi@mail.com");

        assertTrue(otp.matches("\\d{5}"), "OTP harus 5 digit angka, tapi: " + otp);
        verify(valueOps).set("OTP_andi@mail.com", otp, Duration.ofMinutes(5));
        verify(emailService).sendEmail("andi@mail.com", otp);
    }

    // ---------- validateOtpAndGetResetToken ----------

    @Test
    void validateOtp_cocok_menghapusOtpDanMembuatResetToken() {
        when(valueOps.get("OTP_andi@mail.com")).thenReturn("12345");

        String resetToken = service.validateOtpAndGetResetToken("andi@mail.com", "12345");

        assertNotNull(resetToken);
        assertTrue(resetToken.matches("\\d{5}"));
        verify(redisTemplate).delete("OTP_andi@mail.com");
        ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        verify(valueOps).set(key.capture(), eq("andi@mail.com"), eq(Duration.ofMinutes(5)));
        assertEquals("RESET_TOKEN_" + resetToken, key.getValue());
    }

    @Test
    void validateOtp_salah_mengembalikanNull() {
        when(valueOps.get("OTP_andi@mail.com")).thenReturn("12345");

        assertNull(service.validateOtpAndGetResetToken("andi@mail.com", "99999"));
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void validateOtp_kadaluarsa_mengembalikanNull() {
        when(valueOps.get("OTP_andi@mail.com")).thenReturn(null);

        assertNull(service.validateOtpAndGetResetToken("andi@mail.com", "12345"));
    }

    // ---------- reset token ----------

    @Test
    void getEmailByResetToken_membacaDariRedis() {
        when(valueOps.get("RESET_TOKEN_11111")).thenReturn("andi@mail.com");

        assertEquals("andi@mail.com", service.getEmailByResetToken("11111"));
    }

    @Test
    void getEmailByResetToken_tidakAda_null() {
        when(valueOps.get("RESET_TOKEN_11111")).thenReturn(null);

        assertNull(service.getEmailByResetToken("11111"));
    }

    @Test
    void deleteResetToken_menghapusKeyDariRedis() {
        service.deleteResetToken("11111");

        verify(redisTemplate).delete("RESET_TOKEN_11111");
    }

    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
