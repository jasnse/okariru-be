package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.DocumentResponse;
import com.project.binar.okariru.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController controller;

    @TempDir
    Path tempDir;

    private DocumentResponse.getDocumentResponse doc() {
        return new DocumentResponse.getDocumentResponse(1, 3, "ktp.jpg", "abc_ktp.jpg", 7, null, "PENDING");
    }

    @Test
    void findAll() {
        when(documentService.getAllDocument()).thenReturn(List.of(doc()));

        ResponseEntity<List<DocumentResponse.getDocumentResponse>> result = controller.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getDocumentByCustomerAndTransPinjaman() {
        when(documentService.getDocumentByCustomerAndTransPinjaman(7, 3)).thenReturn(List.of(doc()));

        assertEquals(1, controller.getDocumentByCustomerAndTransPinjaman(7, 3).getBody().size());
    }

    @Test
    void downloadDocument_contentTypeDikenali() throws IOException {
        Path file = Files.writeString(tempDir.resolve("catatan.txt"), "halo");
        Resource resource = new FileSystemResource(file);
        when(documentService.loadFileAsResource("catatan.txt")).thenReturn(resource);

        ResponseEntity<Resource> result = controller.downloadDocument("catatan.txt");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(resource, result.getBody());
        assertEquals("inline; filename=\"catatan.txt\"", result.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertNotNull(result.getHeaders().getContentType());
    }

    @Test
    void downloadDocument_contentTypeTidakDikenali_memakaiOctetStream() throws IOException {
        Path file = Files.writeString(tempDir.resolve("data.zzzunknownext"), "halo");
        when(documentService.loadFileAsResource("data.zzzunknownext")).thenReturn(new FileSystemResource(file));

        ResponseEntity<Resource> result = controller.downloadDocument("data.zzzunknownext");

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
    }

    @Test
    void uploadDocument() {
        List<MultipartFile> files = List.of(new MockMultipartFile("file", "a.txt", "text/plain", new byte[]{1}));
        DocumentResponse.documentUploadRespose resp = new DocumentResponse.documentUploadRespose();
        resp.setDokumenId(10);
        when(documentService.uploadDocument(files, 3, 7)).thenReturn(List.of(resp));

        ResponseEntity<List<DocumentResponse.documentUploadRespose>> result = controller.uploadDocument(files, 3, 7);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(10, result.getBody().get(0).getDokumenId());
    }

    @Test
    void deleteDocument_memakaiPesanDariService() {
        when(documentService.deleteDocument(1)).thenReturn("document dengan ID: 1 Telah di hapus");

        ResponseEntity<DocumentResponse.documentDeleteResponse> result = controller.deleteDocument(1);

        verify(documentService).deleteDocument(1);
        assertEquals("document dengan ID: 1 Telah di hapus", result.getBody().getMessage());
    }
}
