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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Override
    public List<DocumentResponse.getDocumentResponse> getDocumentByCustomerAndTransPinjaman(Integer customerId, Integer transPinjamanId) {
        List<DocumentEntity> documentList = documentRepository
                .findByUploadBy_CustomerIdAndTransPinjaman_TransPinjamanId(customerId, transPinjamanId);

        if (documentList.isEmpty()) {
            throw new EntityNotFoundException("Dokumen untuk customer " + customerId + " dan pinjaman transaction " + transPinjamanId + " tidak ditemukan");
        }

        return documentList.stream()
                .map(doc -> {
                    String fileUrl = UriComponentsBuilder
                            .fromPath("/api/v1/document/download")
                            .queryParam("pathfile", doc.getPathfile())
                            .toUriString();

                    return new DocumentResponse.getDocumentResponse(
                            doc.getDokumenId(),
                            doc.getTransPinjaman().getTransPinjamanId(),
                            doc.getNamaFile(),
                            fileUrl,
                            doc.getUploadBy().getCustomerId(),
                            doc.getUploadDate(),
                            doc.getStatusVerification()
                    );
                }) .toList();
    }

    @Override
    @Transactional
    public List<DocumentResponse.documentUploadRespose> uploadDocument(List<MultipartFile> file, Integer transPinjamanId, Integer customerId) {

        List<DocumentResponse.documentUploadRespose> responseList = new ArrayList<>();

       //cek valid input
        PinjamanTransactionEntity transPinjaman = pinjamanTransactionRepository.findById(transPinjamanId)
                .orElseThrow(() -> new EntityNotFoundException("Pinjaman transaction dengan id " + transPinjamanId + " tidak ditemukan"));

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + customerId + " tidak ditemukan"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File tidak boleh kosong");
        }

        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Gagal membuat direktori: " + e.getMessage(), e);
        }

        for (MultipartFile fileItem : file) {
            if (fileItem.isEmpty()) continue;

            //gabungin path root (dir) + nama penyimpanan file unik
            String namaAsli = fileItem.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            String namaUnik = UUID.randomUUID() + "_" + namaAsli;

            try {
                Path target = root.resolve(namaUnik);
                Files.copy(fileItem.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                // Simpan metadata ke Database
                DocumentEntity doc = new DocumentEntity();
                doc.setTransPinjaman(transPinjaman);
                doc.setUploadBy(customer);
                doc.setNamaFile(namaAsli);
                doc.setPathfile(namaUnik);
                doc.setUploadDate(LocalDate.now());
                doc.setStatusVerification("PENDING");

                DocumentEntity savedDoc = documentRepository.save(doc);

                DocumentResponse.documentUploadRespose res = new DocumentResponse.documentUploadRespose();
                res.setDokumenId(savedDoc.getDokumenId());
                res.setNamaFile(savedDoc.getNamaFile());
                res.setPathfile(savedDoc.getPathfile());
                responseList.add(res);
            } catch (IOException e) {
                throw new RuntimeException("Gagal menyimpan file: " + e.getMessage(), e);
            }
        }
        return responseList;
    }

    @Override
    public Resource loadFileAsResource(String pathfile) {
        try {
            Path filePath = resolve(pathfile);
            Resource resource = new UrlResource(filePath.toUri());

            // Cek jika file fisik benar-benar ada di folder storage dan bisa dibaca
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new EntityNotFoundException("File tidak ditemukan atau tidak dapat dibaca: " + pathfile);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Path file tidak valid: " + pathfile, e);
        }
    }

    @Override
    public String deleteDocument(Integer id) {

        //delete document dari DB
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("document dengan Id " + id + " tidak ditemukan"));

        documentRepository.delete(document);

        //Delete document dari folder
        try {
            Files.deleteIfExists(resolve(document.getPathfile()));
        } catch (IOException e) {
            throw new RuntimeException("Gagal menghapus file fisik: " + e.getMessage(), e);
        }

        return "document dengan ID: " + id + " Telah di hapus";
    }

    // cegah path traversal (misal pathfile berisi "../../")
    private Path resolve(String storedFileName) {
        Path resolved = root.resolve(storedFileName).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("Nama file tidak valid");
        }
        return resolved;
    }
}
