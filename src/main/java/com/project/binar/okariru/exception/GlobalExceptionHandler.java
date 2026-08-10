package com.project.binar.okariru.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

//@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

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

    //fallback Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan pada server");
    }
}
