package com.project.binar.okariru.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // dipakai untuk membuat MethodParameter asli
    @SuppressWarnings("unused")
    private void dummy(String param) {
    }

    private MethodParameter methodParameter() throws NoSuchMethodException {
        return new MethodParameter(getClass().getDeclaredMethod("dummy", String.class), 0);
    }

    @Test
    void body_membungkusPesanDenganStatus() {
        Object result = GlobalExceptionHandler.body(HttpStatus.UNAUTHORIZED, "pesan");

        ResponseEntity<?> entity = assertInstanceOf(ResponseEntity.class, result);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals("pesan", entity.getBody());
    }

    @Test
    void handleValidation_mengembalikanPesanFieldPertama() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(new FieldError("req", "nama", "nama harus diisi"));

        ResponseEntity<String> result = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("nama harus diisi", result.getBody());
    }

    @Test
    void handleNotFound_404DenganPesanException() {
        ResponseEntity<String> result = handler.handleNotFound(new EntityNotFoundException("data tidak ada"));

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("data tidak ada", result.getBody());
    }

    @Test
    void handleMissingParam_menyebutNamaParameter() {
        ResponseEntity<String> result =
                handler.handleMissingParam(new MissingServletRequestParameterException("id", "Integer"));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Parameter 'id' wajib diisi", result.getBody());
    }

    @Test
    void handleMissingHeader_menyebutNamaHeader() throws Exception {
        ResponseEntity<String> result =
                handler.handleMissingHeader(new MissingRequestHeaderException("idSearch", methodParameter()));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Header 'idSearch' wajib diisi", result.getBody());
    }

    @Test
    void handleTypeMismatch_menyebutTipeYangDiminta() throws Exception {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", Integer.class, "id", methodParameter(), null);

        ResponseEntity<String> result = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Parameter 'id' harus berupa Integer, tapi menerima 'abc'", result.getBody());
    }

    @Test
    void handleTypeMismatch_tanpaRequiredType_memakaiTeksDefault() throws Exception {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", null, "id", methodParameter(), null);

        ResponseEntity<String> result = handler.handleTypeMismatch(ex);

        assertTrue(result.getBody().contains("harus berupa tipe yang sesuai"));
    }

    @Test
    void handleBodyNotReadable_pesanUmum() {
        ResponseEntity<String> result = handler.handleBodyNotReadable(mock(HttpMessageNotReadableException.class));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Format request body tidak valid"));
    }

    @Test
    void handleIllegalArgument_400DenganPesanException() {
        ResponseEntity<String> result = handler.handleIllegalArgument(new IllegalArgumentException("nominal terlalu besar"));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("nominal terlalu besar", result.getBody());
    }

    @Test
    void handleBadCredentials_401DenganPesanException() {
        ResponseEntity<String> result = handler.handleBadCredentials(new BadCredentialsException("salah"));

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("salah", result.getBody());
    }

    @Test
    void handleGeneral_500TanpaMembocorkanDetailException() {
        ResponseEntity<String> result = handler.handleGeneral(new RuntimeException("detail internal rahasia"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Terjadi kesalahan pada server", result.getBody());
    }

    @ParameterizedTest
    @CsvSource({
            "22001, BAD_REQUEST, 'Ada isian yang melebihi batas panjang, periksa kembali data Anda'",
            "23505, CONFLICT, Data sudah terdaftar (duplikat)",
            "23502, BAD_REQUEST, Ada data wajib yang belum diisi",
            "99999, BAD_REQUEST, 'Data tidak valid, periksa kembali isian Anda'"
    })
    void handleDataIntegrity_memetakanSqlState(String sqlState, HttpStatus status, String pesan) {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("gagal", new SQLException("root", sqlState));

        ResponseEntity<String> result = handler.handleDataIntegrity(ex);

        assertEquals(status, result.getStatusCode());
        assertEquals(pesan, result.getBody());
    }

    @Test
    void handleDataIntegrity_sqlStateNull_pesanDefault() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("gagal", new SQLException("root"));

        ResponseEntity<String> result = handler.handleDataIntegrity(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Data tidak valid, periksa kembali isian Anda", result.getBody());
    }

    @Test
    void handleDataIntegrity_bukanSqlException_pesanDefault() {
        ResponseEntity<String> result = handler.handleDataIntegrity(new DataIntegrityViolationException("gagal"));

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Data tidak valid, periksa kembali isian Anda", result.getBody());
    }
}
