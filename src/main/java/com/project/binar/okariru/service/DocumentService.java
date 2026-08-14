package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.DocumentResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    List<DocumentResponse.getDocumentResponse> getAllDocument();

    List<DocumentResponse.getDocumentResponse> getDocumentByCustomerAndTransPinjaman(Integer customerId, Integer transPinjamanId);

    List<DocumentResponse.documentUploadRespose>  uploadDocument(List<MultipartFile>  file, Integer transPinjamanId, Integer customerId);

    Resource loadFileAsResource(String pathfile);

    String deleteDocument(Integer id);
}
