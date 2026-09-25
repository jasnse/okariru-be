package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.CustomerResponse;
import com.project.binar.okariru.dto.ResetPasswordDTO;
import com.project.binar.okariru.service.CustomerService;
import com.project.binar.okariru.service.jwtAuth.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Reset Password", description = "Alur lupa password customer: kirim OTP, validasi OTP, lalu reset password. Semua endpoint publik (tidak butuh JWT).")
@RestController
@RequestMapping("/api/v1/reset")
@RequiredArgsConstructor
public class ResetPasswordController {
    private final OtpService otpService;
    private final CustomerService customerService;

    @Operation(summary = "Minta kode OTP lupa password", description = "Mengirimkan kode OTP ke email customer untuk memulai alur reset password. Endpoint publik.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP berhasil dikirim ke email", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Kode OTP telah dikirimkan ke email: user@example.com"))),
            @ApiResponse(responseCode = "400", description = "Parameter email tidak dikirim atau email tidak terdaftar", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'email' wajib diisi")))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        return ResponseEntity.ok("Kode OTP telah dikirimkan ke email: " + email);
    }

    @Operation(summary = "Validasi OTP", description = "Memvalidasi kode OTP yang dikirim ke email dan menghasilkan reset token sementara untuk lanjut ke tahap reset password. Endpoint publik.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP valid, reset token diterbitkan", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "OTP Valid. Silakan lanjut ke halaman reset password.",
                              "resetToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "OTP salah atau sudah kadaluarsa, atau parameter email/otp tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "OTP Salah Atau kadaluarsa, Silahkan coba kembal")))
    })
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

    @Operation(summary = "Reset password", description = "Mengganti password customer menggunakan reset token yang didapat dari tahap validasi OTP. Endpoint publik.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password berhasil diubah", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Password berhasil diubah. Silakan login kembali."))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter resetToken tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Password baru wajib diisi"))),
            @ApiResponse(responseCode = "403", description = "Reset token tidak valid: belum melewati validasi OTP atau sesi sudah berakhir", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Akses Ditolak! Anda belum memvalidasi OTP atau sesi Anda telah berakhir."))),
            @ApiResponse(responseCode = "404", description = "Customer dengan email tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan")))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String resetToken,
            @Valid @RequestBody ResetPasswordDTO.resetPasswordRequest request) {

        String email = otpService.getEmailByResetToken(resetToken);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Akses Ditolak! Anda belum memvalidasi OTP atau sesi Anda telah berakhir.");
        }

        //get customerId by Email
        CustomerResponse.getCustomerResponse customer = customerService.getCustomerByEmail(email);

        //resetPassword
        customerService.resetPasswordCustomer(customer.getCustomerId(), request.getNewPassword());
        otpService.deleteResetToken(resetToken);

        return ResponseEntity.ok("Password berhasil diubah. Silakan login kembali.");

    }
}
