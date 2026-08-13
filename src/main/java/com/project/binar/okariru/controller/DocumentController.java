package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.DocumentResponse;
import com.project.binar.okariru.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    //get document by Id
//    @GetMapping(headers = "idDocumentSearch")
//    public ResponseEntity<DocumentResponse.getDocumentResponse> getDocumentById(
//            @RequestHeader("idDocumentSearch") Integer id) {
//        return ResponseEntity.ok(documentService.getDocumentById(id));
//    }

    //upload document
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse.getDocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam Integer transPinjamanId,
            @RequestParam Integer customerId
    ) {
        return ResponseEntity.ok(documentService.uploadDocument(file, transPinjamanId, customerId));
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
