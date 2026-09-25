package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.DocumentResponse;
import com.project.binar.okariru.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Document", description = "Upload, pencarian, download, dan penghapusan dokumen pendukung pengajuan pinjaman")
@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    //get all document
    @Operation(summary = "Ambil semua dokumen", description = "Mengembalikan seluruh data dokumen di sistem. Membutuhkan role SUPERADMIN, MARKETING, BRANCH_MANAGER, atau BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar dokumen", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.getDocumentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<List<DocumentResponse.getDocumentResponse>> findAll() {
        return ResponseEntity.ok(documentService.getAllDocument());
    }

    //get document by customer id + trans pinjaman id
    @Operation(summary = "Ambil dokumen berdasarkan customer & transaksi pinjaman", description = "Mencari dokumen berdasarkan kombinasi customerId dan transPinjamanId. Membutuhkan role SUPERADMIN, MARKETING, BRANCH_MANAGER, atau BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar dokumen", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.getDocumentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter customerId/transPinjamanId tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'customerId' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping(params = {"customerId", "transPinjamanId"})
    public ResponseEntity<List<DocumentResponse.getDocumentResponse>> getDocumentByCustomerAndTransPinjaman(
            @RequestParam Integer customerId,
            @RequestParam Integer transPinjamanId
    ) {
        return ResponseEntity.ok(documentService.getDocumentByCustomerAndTransPinjaman(customerId, transPinjamanId));
    }

    //download / preview file
    @Operation(summary = "Download / preview dokumen", description = "Mengunduh atau mempreview file dokumen berdasarkan path file yang tersimpan di server (Content-Disposition: inline). Membutuhkan role SUPERADMIN, MARKETING, BRANCH_MANAGER, atau BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File berhasil diambil", content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "400", description = "Parameter pathfile tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'pathfile' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "File tidak ditemukan di server", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "File tidak ditemukan")))
    })
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
    @Operation(summary = "Upload dokumen", description = "Mengunggah satu atau lebih file dokumen pendukung untuk sebuah transaksi pinjaman milik customer. Membutuhkan role SUPERADMIN atau CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dokumen berhasil diupload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.documentUploadRespose.class),
                    examples = @ExampleObject(value = """
                            [
                              {
                                "dokumenId": 1,
                                "namaFile": "ktp.jpg",
                                "pathfile": "uploads/ktp.jpg"
                              }
                            ]
                            """))),
            @ApiResponse(responseCode = "400", description = "Parameter file/transPinjamanId/customerId tidak dikirim atau tidak valid", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'transPinjamanId' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman atau customer tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentResponse.documentUploadRespose>> uploadDocument(
            @RequestParam("file") List<MultipartFile> file,
            @RequestParam Integer transPinjamanId,
            @RequestParam Integer customerId
    ) {
        return ResponseEntity.ok(documentService.uploadDocument( file, transPinjamanId, customerId));
    }

    //delete document
    @Operation(summary = "Hapus dokumen", description = "Menghapus data dokumen berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dokumen berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DocumentResponse.documentDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"dokumen dengan Id 1 Telah di hapus\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Dokumen dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "dokumen dengan Id 1 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<DocumentResponse.documentDeleteResponse> deleteDocument(@RequestParam Integer Id) {
        String message = documentService.deleteDocument(Id);

        DocumentResponse.documentDeleteResponse respDelete = new DocumentResponse.documentDeleteResponse();
        respDelete.setMessage(message);
        return ResponseEntity.ok(respDelete);
    }
}
