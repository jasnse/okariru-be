package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.ResetPasswordDTO;
import com.project.binar.okariru.service.CustomerService;
import com.project.binar.okariru.service.jwtAuth.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reset")
@RequiredArgsConstructor
public class ResetPasswordController {
    private final OtpService otpService;
    private final CustomerService customerService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        return ResponseEntity.ok("Kode OTP: " + otp + "telah dikirimkan ke email " + email);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateAndResetToken(@RequestParam String email, @RequestParam String otp) {
        String resetToken = otpService.validateOtpAndGetResetToken(email, otp);
        if (resetToken != null) {
            return ResponseEntity.ok(Map.of(
                    "message", "OTP Valid. Silakan lanjut ke halaman reset password.",
                    "resetToken", resetToken
            ));
        } else  {
            return ResponseEntity.badRequest().body("OTP Salah Atau kadaluarsa, Silahkan coba kembal");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String resetToken,
            @Valid @RequestBody ResetPasswordDTO.resetPasswordRequest request) {

        String email = otpService.getEmailByResetToken(resetToken);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Akses Ditolak! Anda belum memvalidasi OTP atau sesi Anda telah berakhir.");
        }

        customerService.resetPasswordCustomer(request.getCustomerId(), request.getNewPassword());

        otpService.deleteResetToken(resetToken);

        return ResponseEntity.ok("Password berhasil diubah. Silakan login kembali.");

    }
}
