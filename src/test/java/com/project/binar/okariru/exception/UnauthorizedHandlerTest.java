package com.project.binar.okariru.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnauthorizedHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void response_mengisiStatusContentTypeDanEncoding() throws IOException {
        UnauthorizedHandler handler = new UnauthorizedHandler(objectMapper);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.response(response, "Token tidak valid");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(response.getContentType())));
        assertEquals(StandardCharsets.UTF_8.name(), response.getCharacterEncoding());
    }

    @Test
    void response_menulisBodyUnauthorizedKeWriterResponse() throws IOException {
        UnauthorizedHandler handler = new UnauthorizedHandler(objectMapper);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.response(response, "Token tidak valid");

        ArgumentCaptor<Object> body = ArgumentCaptor.forClass(Object.class);
        verify(objectMapper).writeValue(any(Writer.class), body.capture());
        ResponseEntity<?> entity = assertInstanceOf(ResponseEntity.class, body.getValue());
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals("Token tidak valid", entity.getBody());
    }

    @Test
    void commence_memakaiPesanAutentikasiDiperlukan() throws IOException {
        UnauthorizedHandler handler = new UnauthorizedHandler(objectMapper);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.commence(new MockHttpServletRequest(), response, new BadCredentialsException("x"));

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        ArgumentCaptor<Object> body = ArgumentCaptor.forClass(Object.class);
        verify(objectMapper).writeValue(any(Writer.class), body.capture());
        ResponseEntity<?> entity = assertInstanceOf(ResponseEntity.class, body.getValue());
        assertEquals(GlobalExceptionHandler.PESAN_AUTENTIKASI_DIPERLUKAN, entity.getBody());
    }
}
