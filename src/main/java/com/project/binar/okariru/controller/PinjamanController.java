package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PinjamanRequest;
import com.project.binar.okariru.dto.PinjamanResponse;
import com.project.binar.okariru.service.PinjamanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pinjaman", description = "Kelola master data produk pinjaman (jenis pinjaman, bunga, biaya lainnya)")
@RestController
@RequestMapping("/api/v1/pinjaman")
@RequiredArgsConstructor
public class PinjamanController {
    private final PinjamanService pinjamanService;

    //get all pinjaman
//    @GetMapping
//    public ResponseEntity<List<PinjamanResponse.getPinjamanResponse>> getAllPinjaman() {
//        return ResponseEntity.ok(pinjamanService.getAllPinjaman());
//    }

    //get all pinjaman with pagination
    @Operation(summary = "Ambil semua produk pinjaman (paginated)", description = "Mengembalikan daftar produk pinjaman dengan pagination, bisa difilter dengan keyword. Bisa diakses oleh MARKETING, BRANCH_MANAGER, BACKOFFICE, CUSTOMER, atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar pinjaman", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanResponse.getPinjamanResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<PinjamanResponse.getPinjamanResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return ResponseEntity.ok(pinjamanService.findAll(keyword, page, size));
    }

    //get pinjaman by Id
    @Operation(summary = "Ambil produk pinjaman berdasarkan Id", description = "Mencari satu data produk pinjaman berdasarkan Id yang dikirim lewat header idPinjamanSearch. Bisa diakses oleh MARKETING, BRANCH_MANAGER, BACKOFFICE, CUSTOMER, atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pinjaman ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanResponse.getPinjamanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idPinjamanSearch tidak dikirim atau tidak valid", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idPinjamanSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman dengan Id 2 tidak ditemukan")))
    })
    @GetMapping(headers = "idPinjamanSearch")
    public ResponseEntity<PinjamanResponse.getPinjamanResponse> getPinjamanById(
            @Parameter(description = "Id produk pinjaman yang dicari, dikirim lewat header idPinjamanSearch") @Valid @RequestHeader("idPinjamanSearch") Integer id) {
        return ResponseEntity.ok(pinjamanService.getPinjamanById(id));
    }

    //add pinjaman
    @Operation(summary = "Tambah produk pinjaman", description = "Membuat data produk pinjaman baru. Membutuhkan role SUPERADMIN atau BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pinjaman berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanResponse.getPinjamanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "jenis pinjaman harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (SUPERADMIN/BACKOFFICE saja)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @PostMapping
    public ResponseEntity<PinjamanResponse.getPinjamanResponse> addPinjaman(
            @Valid @RequestBody PinjamanRequest.pinjamanAddRequest request) {
        return ResponseEntity.ok(pinjamanService.addPinjaman(request));
    }

    //update pinjaman
    @Operation(summary = "Update produk pinjaman", description = "Memperbarui data produk pinjaman berdasarkan Id. Membutuhkan role SUPERADMIN atau BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pinjaman berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanResponse.pinjamanUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Pinjaman Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "bunga harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (SUPERADMIN/BACKOFFICE saja)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman dengan Id 2 tidak ditemukan")))
    })
    @PutMapping
    public ResponseEntity<PinjamanResponse.pinjamanUpdateResponse> updatePinjaman(
            @Valid
            @RequestParam Integer id,
            @Valid @RequestBody PinjamanRequest.pinjamanUpdateRequest request
    ) {
        pinjamanService.updatePinjaman(id, request.jenisPinjaman, request.deskripsiPinjaman, request.bunga, request.biayaLainnya);

        PinjamanResponse.pinjamanUpdateResponse respUpdate = new PinjamanResponse.pinjamanUpdateResponse();
        respUpdate.setMessage("Pinjaman Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete pinjaman
    @Operation(summary = "Hapus produk pinjaman", description = "Menghapus data produk pinjaman berdasarkan Id. Membutuhkan role SUPERADMIN atau BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pinjaman berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanResponse.pinjamanDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete pinjaman successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (SUPERADMIN/BACKOFFICE saja)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman dengan Id 2 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<PinjamanResponse.pinjamanDeleteResponse> deletePinjaman(
            @Valid @RequestParam Integer Id) {
        pinjamanService.deletePinjaman(Id);

        PinjamanResponse.pinjamanDeleteResponse respDelete = new PinjamanResponse.pinjamanDeleteResponse();
        respDelete.setMessage("Delete pinjaman successfully");
        return ResponseEntity.ok(respDelete);
    }
}
