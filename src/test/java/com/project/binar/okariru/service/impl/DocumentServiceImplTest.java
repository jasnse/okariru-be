package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.DocumentResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.DocumentEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.DocumentRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private PinjamanTransactionRepository pinjamanTransactionRepository;
    @Mock
    private CustomerRepository customerRepository;

    @TempDir
    Path tempDir;

    private DocumentServiceImpl service;
    private CustomerEntity customer;
    private PinjamanTransactionEntity trx;

    @BeforeEach
    void setUp() {
        service = new DocumentServiceImpl(documentRepository, pinjamanTransactionRepository, customerRepository,
                tempDir.toString());

        customer = new CustomerEntity();
        customer.setCustomerId(7);
        trx = new PinjamanTransactionEntity();
        trx.setTransPinjamanId(3);
    }

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    private DocumentEntity document(int id, String pathfile) {
        DocumentEntity d = new DocumentEntity();
        d.setDokumenId(id);
        d.setTransPinjaman(trx);
        d.setUploadBy(customer);
        d.setNamaFile("ktp.jpg");
        d.setPathfile(pathfile);
        d.setUploadDate(LocalDate.of(2026, 1, 1));
        d.setStatusVerification("PENDING");
        return d;
    }

    // ---------- getAllDocument ----------

    @Test
    void getAllDocument_memetakanSemua() {
        when(documentRepository.findAll()).thenReturn(List.of(document(1, "abc_ktp.jpg")));

        List<DocumentResponse.getDocumentResponse> result = service.getAllDocument();

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getTransPinjamanId());
        assertEquals(7, result.get(0).getUploadBy());
        assertEquals("abc_ktp.jpg", result.get(0).getPathfile());
    }

    // ---------- getDocumentByCustomerAndTransPinjaman ----------

    @Test
    void getDocumentByCustomerAndTransPinjaman_kosong_melemparNotFound() {
        when(documentRepository.findByUploadBy_CustomerIdAndTransPinjaman_TransPinjamanId(7, 3)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> service.getDocumentByCustomerAndTransPinjaman(7, 3));
    }

    @Test
    void getDocumentByCustomerAndTransPinjaman_membangunUrlDownload() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(documentRepository.findByUploadBy_CustomerIdAndTransPinjaman_TransPinjamanId(7, 3))
                .thenReturn(List.of(document(1, "abc_ktp.jpg")));

        List<DocumentResponse.getDocumentResponse> result = service.getDocumentByCustomerAndTransPinjaman(7, 3);

        assertEquals(1, result.size());
        String url = result.get(0).getPathfile();
        assertTrue(url.contains("/api/v1/document/download"));
        assertTrue(url.contains("pathfile=abc_ktp.jpg"));
    }

    // ---------- uploadDocument ----------

    @Test
    void uploadDocument_transaksiTidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.uploadDocument(List.of(), 3, 7));
    }

    @Test
    void uploadDocument_customerTidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.uploadDocument(List.of(), 3, 7));
    }

    @Test
    void uploadDocument_listKosong() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        assertThrows(IllegalArgumentException.class, () -> service.uploadDocument(List.of(), 3, 7));
    }

    @Test
    void uploadDocument_berhasil_menyimpanFileDanMelewatiFileKosong() throws IOException {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(inv -> {
            DocumentEntity d = inv.getArgument(0);
            d.setDokumenId(10);
            return d;
        });
        MockMultipartFile ktp = new MockMultipartFile("file", "ktp saya (1).jpg", "image/jpeg",
                "isi-ktp".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile kosong = new MockMultipartFile("file", "kosong.jpg", "image/jpeg", new byte[0]);

        List<DocumentResponse.documentUploadRespose> result = service.uploadDocument(List.of(ktp, kosong), 3, 7);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getDokumenId());
        // nama asli disanitasi: karakter selain [a-zA-Z0-9._-] jadi "_"
        assertEquals("ktp_saya__1_.jpg", result.get(0).getNamaFile());
        assertTrue(result.get(0).getPathfile().endsWith("_ktp_saya__1_.jpg"));
        Path saved = tempDir.resolve(result.get(0).getPathfile());
        assertTrue(Files.exists(saved));
        assertEquals("isi-ktp", Files.readString(saved));
        verify(documentRepository, times(1)).save(any(DocumentEntity.class));
    }

    @Test
    void uploadDocument_statusAwalPending() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        service.uploadDocument(List.of(new MockMultipartFile("file", "a.pdf", "application/pdf", new byte[]{1})), 3, 7);

        verify(documentRepository).save(argThat(d -> "PENDING".equals(d.getStatusVerification())
                && d.getUploadBy() == customer && d.getTransPinjaman() == trx));
    }

    @Test
    void uploadDocument_gagalMembacaFile_melemparRuntimeException() throws IOException {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        MultipartFile rusak = mock(MultipartFile.class);
        when(rusak.isEmpty()).thenReturn(false);
        when(rusak.getOriginalFilename()).thenReturn("a.txt");
        when(rusak.getInputStream()).thenThrow(new IOException("disk penuh"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.uploadDocument(List.of(rusak), 3, 7));
        assertTrue(ex.getMessage().contains("Gagal menyimpan file"));
        verify(documentRepository, never()).save(any());
    }

    @Test
    void uploadDocument_gagalMembuatDirektori_melemparRuntimeException() throws IOException {
        Path bukanDirektori = Files.createFile(tempDir.resolve("file-biasa"));
        DocumentServiceImpl rusak = new DocumentServiceImpl(documentRepository, pinjamanTransactionRepository,
                customerRepository, bukanDirektori.toString());
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", "text/plain", new byte[]{1});

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rusak.uploadDocument(List.of(file), 3, 7));
        assertTrue(ex.getMessage().contains("Gagal membuat direktori"));
    }

    // ---------- loadFileAsResource ----------

    @Test
    void loadFileAsResource_fileAda() throws IOException {
        Files.writeString(tempDir.resolve("ada.txt"), "halo");

        Resource resource = service.loadFileAsResource("ada.txt");

        assertTrue(resource.exists());
        assertEquals("ada.txt", resource.getFilename());
    }

    @Test
    void loadFileAsResource_fileTidakAda() {
        assertThrows(EntityNotFoundException.class, () -> service.loadFileAsResource("tidak-ada.txt"));
    }

    @Test
    void loadFileAsResource_pathTraversalDitolak() {
        assertThrows(IllegalArgumentException.class, () -> service.loadFileAsResource("../rahasia.txt"));
    }

    // ---------- deleteDocument ----------

    @Test
    void deleteDocument_berhasil_menghapusRecordDanFileFisik() throws IOException {
        Path file = Files.writeString(tempDir.resolve("abc_ktp.jpg"), "isi");
        DocumentEntity doc = document(1, "abc_ktp.jpg");
        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));

        String msg = service.deleteDocument(1);

        assertTrue(msg.contains("1"));
        verify(documentRepository).delete(doc);
        assertFalse(Files.exists(file));
    }

    @Test
    void deleteDocument_fileFisikSudahTidakAda_tetapBerhasil() {
        DocumentEntity doc = document(1, "hilang.jpg");
        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));

        assertDoesNotThrow(() -> service.deleteDocument(1));
        verify(documentRepository).delete(doc);
    }

    @Test
    void deleteDocument_tidakDitemukan() {
        when(documentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteDocument(1));
        verify(documentRepository, never()).delete(any());
    }

    @Test
    void deleteDocument_gagalMenghapusFileFisik_melemparRuntimeException() throws IOException {
        // "folder" berisi file -> Files.deleteIfExists melempar DirectoryNotEmptyException
        Path folder = Files.createDirectory(tempDir.resolve("folder"));
        Files.writeString(folder.resolve("isi.txt"), "x");
        DocumentEntity doc = document(1, "folder");
        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteDocument(1));
        assertTrue(ex.getMessage().contains("Gagal menghapus file fisik"));
    }

    @Test
    void deleteDocument_pathTraversalDitolak() {
        DocumentEntity doc = document(1, "../rahasia.txt");
        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));

        assertThrows(IllegalArgumentException.class, () -> service.deleteDocument(1));
    }
}
