package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.DocumentResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.DocumentEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.DocumentRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import com.project.binar.okariru.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    //init
    private final DocumentRepository documentRepository;
    private final PinjamanTransactionRepository pinjamanTransactionRepository;
    private final CustomerRepository customerRepository;
    private final Path root;

    //constructor
    DocumentServiceImpl(DocumentRepository documentRepository,
                         PinjamanTransactionRepository pinjamanTransactionRepository,
                         CustomerRepository customerRepository,
                         @Value("${app.storage.image-dir}") String dir) {
        this.documentRepository = documentRepository;
        this.pinjamanTransactionRepository = pinjamanTransactionRepository;
        this.customerRepository = customerRepository;
        this.root = Paths.get(dir).toAbsolutePath().normalize();
    }

    @Override
    public List<DocumentResponse.getDocumentResponse> getAllDocument() {
        return documentRepository.findAll()
                .stream()
                .map(document -> new DocumentResponse.getDocumentResponse(
                        document.getDokumenId(),
                        document.getTransPinjaman().getTransPinjamanId(),
                        document.getNamaFile(),
                        document.getPathfile(),
                        document.getUploadBy().getCustomerId(),
                        document.getUploadDate(),
                        document.getStatusVerification()
                ))
                .toList();
    }

//    @Override
//    public DocumentResponse.getDocumentResponse getDocumentById(Integer id) {
//        DocumentEntity document = documentRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("document dengan Id " + id + " tidak ditemukan"));
//        return new DocumentResponse.getDocumentResponse(
//                document.getDokumenId(),
//                document.getTransPinjaman().getTransPinjamanId(),
//                document.getNamaFile(),
//                document.getPathfile(),
//                document.getUploadBy().getCustomerId(),
//                document.getUploadDate(),
//                document.getStatusVerification()
//        );
//    }

    @Override
    @Transactional
    public DocumentResponse.getDocumentResponse uploadDocument(MultipartFile file, Integer transPinjamanId, Integer customerId) {

       //cek valid input
        PinjamanTransactionEntity transPinjaman = pinjamanTransactionRepository.findById(transPinjamanId)
                .orElseThrow(() -> new EntityNotFoundException("Pinjaman transaction dengan id " + transPinjamanId + " tidak ditemukan"));

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + customerId + " tidak ditemukan"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File tidak boleh kosong");
        }



        //gabungin path root (dir) + nama penyimpanan file unik
        String namaAsli = file.getOriginalFilename();
        String namaUnik = UUID.randomUUID() + "_" + namaAsli;

        try {
            Files.createDirectories(root);
            Path target = root.resolve(namaUnik);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage(), e);
        }

        //set dan save file
        DocumentEntity document = new DocumentEntity();
        document.setTransPinjaman(transPinjaman);
        document.setUploadBy(customer);
        document.setNamaFile(namaAsli);
        document.setPathfile(namaUnik);
        document.setUploadDate(LocalDate.now());
        document.setStatusVerification("Menunggu Verifikasi");

        DocumentEntity saved = documentRepository.save(document);
        return new DocumentResponse.getDocumentResponse(
                saved.getDokumenId(),
                saved.getTransPinjaman().getTransPinjamanId(),
                saved.getNamaFile(),
                saved.getPathfile(),
                saved.getUploadBy().getCustomerId(),
                saved.getUploadDate(),
                saved.getStatusVerification()
        );
    }

    @Override
    public String deleteDocument(Integer id) {

        //delete document dari DB
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("document dengan Id " + id + " tidak ditemukan"));

        documentRepository.delete(document);

//        Delete document dari folder
        try {
            Files.deleteIfExists(root.resolve(document.getPathfile()));
        } catch (IOException e) {
            throw new RuntimeException("Gagal menghapus file fisik: " + e.getMessage(), e);
        }

        return "document dengan ID: " + id + " Telah di hapus";
    }
}
