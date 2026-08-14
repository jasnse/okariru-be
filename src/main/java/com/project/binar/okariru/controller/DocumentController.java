package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.DocumentResponse;
import com.project.binar.okariru.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    //get all document
    @GetMapping
    public ResponseEntity<List<DocumentResponse.getDocumentResponse>> findAll() {
        return ResponseEntity.ok(documentService.getAllDocument());
    }

    //get document by customer id + trans pinjaman id
    @GetMapping(params = {"customerId", "transPinjamanId"})
    public ResponseEntity<List<DocumentResponse.getDocumentResponse>> getDocumentByCustomerAndTransPinjaman(
            @RequestParam Integer customerId,
            @RequestParam Integer transPinjamanId
    ) {
        return ResponseEntity.ok(documentService.getDocumentByCustomerAndTransPinjaman(customerId, transPinjamanId));
    }

    //download / preview file
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadDocument(
            @RequestParam String pathfile) throws IOException {
        Resource resource = documentService.loadFileAsResource(pathfile);

        // Deteksi Content-Type secara dinamis (PDF, PNG, JPG, dll)
        String contentType = Files.probeContentType(resource.getFile().toPath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // "inline" agar file bisa dipreview langsung di Postman/Browser
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    //upload document
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentResponse.documentUploadRespose>> uploadDocument(
            @RequestParam("file") List<MultipartFile> file,
            @RequestParam Integer transPinjamanId,
            @RequestParam Integer customerId
    ) {
        return ResponseEntity.ok(documentService.uploadDocument( file, transPinjamanId, customerId));
    }

    //delete document
    @DeleteMapping
    public ResponseEntity<DocumentResponse.documentDeleteResponse> deleteDocument(@RequestParam Integer Id) {
        String message = documentService.deleteDocument(Id);

        DocumentResponse.documentDeleteResponse respDelete = new DocumentResponse.documentDeleteResponse();
        respDelete.setMessage(message);
        return ResponseEntity.ok(respDelete);
    }
}
