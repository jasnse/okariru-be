package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    List<DocumentResponse.getDocumentResponse> getAllDocument();

//    DocumentResponse.getDocumentResponse getDocumentById(Integer id);

    DocumentResponse.getDocumentResponse uploadDocument(MultipartFile file, Integer transPinjamanId, Integer customerId);

    String deleteDocument(Integer id);
}
