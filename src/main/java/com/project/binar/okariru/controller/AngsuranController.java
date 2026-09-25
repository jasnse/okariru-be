package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.AngsuranRequest;
import com.project.binar.okariru.dto.AngsuranResponse;
import com.project.binar.okariru.service.AngsuranService;
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

@Tag(name = "Angsuran", description = "Kelola jadwal & pembayaran angsuran (cicilan) dari transaksi pinjaman")
@RestController
@RequestMapping("/api/v1/angsuran")
@RequiredArgsConstructor
public class AngsuranController {
    private final AngsuranService angsuranService;

    //get all angsuran
    @Operation(summary = "Ambil semua angsuran", description = "Mengembalikan seluruh data angsuran di sistem. Membutuhkan role CUSTOMER atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar angsuran", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.getAngsuranResponse.class),
                    examples = @ExampleObject(value = """
                            [
                              {
                                "angsuranId": 1,
                                "transPinjamanId": 5,
                                "jumlahPokok": 1000000,
                                "jumlahBunga": 15000.0,
                                "totalAngsuran": 1015000,
                                "tanggalJatuhTempo": "2026-01-10",
                                "statusAngsuran": "Belum Lunas",
                                "tenor": 1,
                                "sisaTagihan": 1015000
                              }
                            ]
                            """))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<List<AngsuranResponse.getAngsuranResponse>> findAll() {
        return ResponseEntity.ok(angsuranService.getAllAngsuran());
    }

    //get angsuran by Id
    @Operation(summary = "Ambil angsuran berdasarkan Id", description = "Mencari satu data angsuran berdasarkan Id yang dikirim lewat header idAngsuranSearch. Membutuhkan role CUSTOMER atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Angsuran ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.getAngsuranResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "angsuranId": 1,
                              "transPinjamanId": 5,
                              "jumlahPokok": 1000000,
                              "jumlahBunga": 15000.0,
                              "totalAngsuran": 1015000,
                              "tanggalJatuhTempo": "2026-01-10",
                              "statusAngsuran": "Belum Lunas",
                              "tenor": 1,
                              "sisaTagihan": 1015000
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Header idAngsuranSearch tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idAngsuranSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Angsuran dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "angsuran dengan Id 99 tidak ditemukan")))
    })
    @GetMapping(headers = "idAngsuranSearch")
    public ResponseEntity<AngsuranResponse.getAngsuranResponse> getAngsuranById(
            @Parameter(description = "Id angsuran yang dicari, dikirim lewat header idAngsuranSearch") @RequestHeader("idAngsuranSearch") Integer id) {
        return ResponseEntity.ok(angsuranService.getAngsuranById(id));
    }

    //get jadwal angsuran (semua tenor) milik satu pinjaman transaction
    @Operation(summary = "Ambil jadwal angsuran per transaksi pinjaman", description = "Mengembalikan seluruh angsuran (semua tenor) milik satu transaksi pinjaman. Membutuhkan role CUSTOMER atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil jadwal angsuran", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.getAngsuranResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping("/pinjaman/{transPinjamanId}")
    public ResponseEntity<List<AngsuranResponse.getAngsuranResponse>> getAngsuranByTransPinjaman(
            @PathVariable Integer transPinjamanId) {
        return ResponseEntity.ok(angsuranService.getAngsuranByTransPinjaman(transPinjamanId));
    }

    //add angsuran
    @Operation(summary = "Tambah angsuran", description = "Membuat satu data angsuran secara manual. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Angsuran berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.getAngsuranResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "jumlah pokok harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @PostMapping
    public ResponseEntity<AngsuranResponse.getAngsuranResponse> addAngsuran(
            @Valid @RequestBody AngsuranRequest.angsuranAddRequest request) {
        return ResponseEntity.ok(angsuranService.addAngsuran(request));
    }

    //generate angsuran otomatis sesuai tenor
    @Operation(summary = "Generate angsuran otomatis", description = "Membuat jadwal angsuran secara otomatis sesuai tenor untuk satu transaksi pinjaman. Membutuhkan role BACKOFFICE atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jadwal angsuran berhasil digenerate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.getAngsuranResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "tenor harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @PostMapping("/generate")
    public ResponseEntity<List<AngsuranResponse.getAngsuranResponse>> generateAngsuran(
            @Valid @RequestBody AngsuranRequest.angsuranGenerateRequest request) {
        return ResponseEntity.ok(angsuranService.generateAngsuran(request));
    }

    //bayar angsuran (otomatis dialokasikan ke semua angsuran yang belum lunas, urut tenor)
    @Operation(summary = "Bayar angsuran", description = "Menerima pembayaran dan otomatis mengalokasikannya ke angsuran yang belum lunas secara berurutan sesuai tenor. Hanya bisa diakses oleh CUSTOMER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pembayaran berhasil diproses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.angsuranBayarResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Pembayaran angsuran berhasil\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi gagal atau nominal bayar tidak sesuai", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "nominal bayar harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya CUSTOMER)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @PostMapping("/bayar")
    public ResponseEntity<AngsuranResponse.angsuranBayarResponse> bayarAngsuran(
            @Valid @RequestBody AngsuranRequest.angsuranBayarRequest request
    ) {
        return ResponseEntity.ok(angsuranService.bayarAngsuran(request));
    }

    //update angsuran
    @Operation(summary = "Update angsuran", description = "Memperbarui data angsuran berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Angsuran berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.angsuranUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Angsuran Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Angsuran dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "angsuran dengan Id 99 tidak ditemukan")))
    })
    @PutMapping
    public ResponseEntity<AngsuranResponse.angsuranUpdateResponse> updateAngsuran(
            @RequestParam Integer id,
            @Valid @RequestBody AngsuranRequest.angsuranUpdateRequest request
    ) {
        angsuranService.updateAngsuran(id, request.transPinjamanId, request.jumlahPokok, request.jumlahBunga,
                request.totalAngsuran, request.tanggalJatuhTempo, request.statusAngsuran, request.tenor);

        AngsuranResponse.angsuranUpdateResponse respUpdate = new AngsuranResponse.angsuranUpdateResponse();
        respUpdate.setMessage("Angsuran Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete angsuran
    @Operation(summary = "Hapus angsuran", description = "Menghapus data angsuran berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Angsuran berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AngsuranResponse.angsuranDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete angsuran successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Angsuran dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "angsuran dengan Id 99 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<AngsuranResponse.angsuranDeleteResponse> deleteAngsuran(@RequestParam Integer Id) {
            angsuranService.deleteAngsuran(Id);

        AngsuranResponse.angsuranDeleteResponse respDelete = new AngsuranResponse.angsuranDeleteResponse();
        respDelete.setMessage("Delete angsuran successfully");
        return ResponseEntity.ok(respDelete);
    }
}
