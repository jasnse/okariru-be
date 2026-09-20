package com.project.binar.okariru.service.jwtAuth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @Test
    void sendEmail_mengirimOtpKeAlamatTujuan() {
        service.sendEmail("andi@mail.com", "12345");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();
        assertArrayEquals(new String[]{"andi@mail.com"}, msg.getTo());
        assertEquals("Kode OTP Verifikasi Anda", msg.getSubject());
        assertTrue(msg.getText().contains("12345"));
        assertTrue(msg.getText().contains("5 menit"));
    }
}
