package com.project.binar.okariru.service.jwtAuth;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String otp) {

        String emailText = "Halo,\n\n" +
                "Kami menerima permintaan untuk melakukan reset password akun Anda. " +
                "Gunakan kode OTP berikut untuk melanjutkan:\n\n" +
                "   " + otp + "   \n\n" +
                "Kode ini berlaku selama 5 menit. Jangan berikan kode ini kepada siapa pun.\n\n" +
                "Jika Anda tidak merasa melakukan permintaan ini, silakan abaikan email ini.\n\n" +
                "Terima kasih,\n";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kode OTP Verifikasi Anda");
        message.setText(emailText);

        mailSender.send(message);
    }
}
