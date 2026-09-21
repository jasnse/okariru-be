package com.project.binar.okariru.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;

//@ControllerAdvice
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String PESAN_AUTENTIKASI_DIPERLUKAN = "error autentikasi";

    public static Object body(HttpStatus httpStatus, String pesan) {
        return new ResponseEntity<>(pesan, httpStatus);
    }

    //validasi @Valid gagal
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    //error yang data gak ketemu
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    //@RequestParam kosong error
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body("Parameter '" + e.getParameterName() + "' wajib diisi");
    }

    //@RequestHeader kosong error
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingHeader(MissingRequestHeaderException e) {
        return ResponseEntity.badRequest().body("Header '" + e.getHeaderName() + "' wajib diisi");
    }

    //Input gak sesuai tipe data
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "tipe yang sesuai";
        return ResponseEntity.badRequest().body(
                "Parameter '" + e.getName() + "' harus berupa " + requiredType + ", tapi menerima '" + e.getValue() + "'"
        );
    }

    // JSON malformed
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleBodyNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body("Format request body tidak valid, cek kembali tipe data yang dikirim");
    }

    //validasi business rule gagal (misal nominal bayar melebihi maksimal)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    //username/password salah (dilempar langsung dari LoginService, bukan lewat AuthenticationManager)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    //constraint database dilanggar (data terlalu panjang, duplikat, kolom required kosong, dll)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityViolationException e) {
        Throwable root = e.getMostSpecificCause();
        log.error("Data integrity violation: {}", root.getMessage(), e);

        String sqlState = root instanceof SQLException sql && sql.getSQLState() != null ? sql.getSQLState() : "";
        String pesan = switch (sqlState) {
            case "22001" -> "Ada isian yang melebihi batas panjang, periksa kembali data Anda";
            case "23505" -> "Data sudah terdaftar (duplikat)";
            case "23502" -> "Ada data wajib yang belum diisi";
            default -> "Data tidak valid, periksa kembali isian Anda";
        };
        HttpStatus status = "23505".equals(sqlState) ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(pesan);
    }

    //fallback Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan pada server");
    }
}
