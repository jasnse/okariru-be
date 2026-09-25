package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PlafondRequest;
import com.project.binar.okariru.dto.PlafondResponse;
import com.project.binar.okariru.service.PlafondService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Plafond", description = "Kelola plafond (limit pinjaman) yang dimiliki tiap customer")
@RestController
@RequestMapping("/api/v1/plafond")
@RequiredArgsConstructor
public class PlafondController {
    private final PlafondService plafondService;

    //get all plafond
    @Operation(summary = "Ambil semua plafond", description = "Mengembalikan seluruh data plafond di sistem. Bisa diakses oleh SUPERADMIN, BACKOFFICE, atau CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar plafond", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlafondResponse.getPlafondResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<List<PlafondResponse.getPlafondResponse>> findAll() {
        return ResponseEntity.ok(plafondService.getAllPlafond());
    }

    //get plafond by customer id (dipakai di modal detail plafond per-customer)
    @Operation(summary = "Ambil plafond berdasarkan Id customer", description = "Mencari data plafond milik satu customer berdasarkan userId, dipakai untuk modal detail plafond per-customer. Bisa diakses oleh SUPERADMIN, BACKOFFICE, atau CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plafond ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlafondResponse.getPlafondResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter userId tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'userId' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Plafond untuk customer tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "plafond untuk customer dengan Id 5 tidak ditemukan")))
    })
    @GetMapping(params = "userId")
    public ResponseEntity<PlafondResponse.getPlafondResponse> getPlafondByCustomerId(
            @RequestParam Integer userId) {
        return ResponseEntity.ok(plafondService.getPlafondByCustomerId(userId));
    }

    //get plafond by Id
    @Operation(summary = "Ambil plafond berdasarkan Id", description = "Mencari satu data plafond berdasarkan Id yang dikirim lewat header idPlafondSearch. Bisa diakses oleh SUPERADMIN, BACKOFFICE, atau CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plafond ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlafondResponse.getPlafondResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idPlafondSearch tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idPlafondSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Plafond dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "plafond dengan Id 1 tidak ditemukan")))
    })
    @GetMapping(headers = "idPlafondSearch")
    public ResponseEntity<PlafondResponse.getPlafondResponse> getPlafondById(
            @Parameter(description = "Id plafond yang dicari, dikirim lewat header idPlafondSearch") @RequestHeader("idPlafondSearch") Integer id) {
        return ResponseEntity.ok(plafondService.getPlafondById(id));
    }

    //add plafond
    @Operation(summary = "Tambah plafond", description = "Membuat data plafond baru untuk seorang customer. Membutuhkan role BACKOFFICE atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plafond berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlafondResponse.getPlafondResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "total plafond harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (BACKOFFICE/SUPERADMIN saja)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer (userId) tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "customer dengan Id 5 tidak ditemukan"))),
            @ApiResponse(responseCode = "409", description = "Customer sudah memiliki plafond (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PostMapping
    public ResponseEntity<PlafondResponse.getPlafondResponse> addPlafond(
            @Valid @RequestBody PlafondRequest.plafondAddRequest request) {
        return ResponseEntity.ok(plafondService.addPlafond(request));
    }

    //update plafond
    @Operation(summary = "Update plafond", description = "Memperbarui data plafond berdasarkan Id. Membutuhkan role BACKOFFICE atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plafond berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlafondResponse.plafondUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Plafond Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "total plafond harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (BACKOFFICE/SUPERADMIN saja)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Plafond atau customer (userId) tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "plafond dengan Id 1 tidak ditemukan")))
    })
    @PutMapping
    public ResponseEntity<PlafondResponse.plafondUpdateResponse> updatePlafond(
            @RequestParam Integer id,
            @Valid @RequestBody PlafondRequest.plafondUpdateRequest request
    ) {
        plafondService.updatePlafond(id, request.userId, request.totalPlafond, request.deskripsiPlafond, request.updatedBy);

        PlafondResponse.plafondUpdateResponse respUpdate = new PlafondResponse.plafondUpdateResponse();
        respUpdate.setMessage("Plafond Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete plafond
    @Operation(summary = "Hapus plafond", description = "Menghapus data plafond berdasarkan Id. Hanya bisa diakses oleh role BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plafond berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlafondResponse.plafondDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete plafond successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya BACKOFFICE)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Plafond dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "plafond dengan Id 1 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<PlafondResponse.plafondDeleteResponse> deletePlafond(@RequestParam Integer Id) {
        plafondService.deletePlafond(Id);

        PlafondResponse.plafondDeleteResponse respDelete = new PlafondResponse.plafondDeleteResponse();
        respDelete.setMessage("Delete plafond successfully");
        return ResponseEntity.ok(respDelete);
    }
}
