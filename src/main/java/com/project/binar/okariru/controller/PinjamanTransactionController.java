package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import com.project.binar.okariru.service.PinjamanTransactionService;
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

@Tag(name = "Pinjaman Transaction", description = "Kelola alur pengajuan pinjaman customer: submit, review (MARKETING), approval (BRANCH_MANAGER), pencairan/disburse (BACKOFFICE)")
@RestController
@RequestMapping("/api/v1/pinjaman/transaction")
@RequiredArgsConstructor
public class PinjamanTransactionController {
    private final PinjamanTransactionService pinjamanTransactionService;

    //get all pinjaman transaction
//    @GetMapping
//    public ResponseEntity<List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse>> findAll() {
//        return ResponseEntity.ok(pinjamanTransactionService.getAllPinjamanTransaction());
//    }

    @Operation(summary = "Ambil semua transaksi pinjaman (paginated)", description = "Mengembalikan daftar transaksi pinjaman dengan pagination, bisa difilter berdasarkan status dan/atau keyword. Bisa diakses oleh MARKETING, BRANCH_MANAGER, BACKOFFICE, CUSTOMER, atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar transaksi pinjaman", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.getPinjamanTransactionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return ResponseEntity.ok(pinjamanTransactionService.findAll(status,keyword, page, size));
    }

    //get pinjaman transaction list by customer
    @Operation(summary = "Ambil transaksi pinjaman milik customer tertentu", description = "Mengembalikan daftar transaksi pinjaman milik satu customer, bisa difilter berdasarkan status dan/atau keyword. Bisa diakses oleh MARKETING, BRANCH_MANAGER, BACKOFFICE, CUSTOMER, atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar transaksi pinjaman", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.getPinjamanTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter customerId tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'customerId' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping("/customer")
    public ResponseEntity<List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse>> findByCustomerId(
            @RequestParam Integer customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ){
        return ResponseEntity.ok(pinjamanTransactionService.findByCustomerId(customerId, status, keyword));
    }

    //get pinjaman transaction by Id
    @Operation(summary = "Ambil transaksi pinjaman berdasarkan Id", description = "Mencari satu transaksi pinjaman berdasarkan Id yang dikirim lewat header idPinjamanTransactionSearch. Bisa diakses oleh MARKETING, BRANCH_MANAGER, BACKOFFICE, CUSTOMER, atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaksi pinjaman ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.getPinjamanTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idPinjamanTransactionSearch tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idPinjamanTransactionSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @GetMapping(headers = "idPinjamanTransactionSearch")
    public ResponseEntity<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> getPinjamanTransactionById(
            @Parameter(description = "Id transaksi pinjaman yang dicari, dikirim lewat header idPinjamanTransactionSearch") @RequestHeader("idPinjamanTransactionSearch") Integer id) {
        return ResponseEntity.ok(pinjamanTransactionService.getPinjamanTransactionById(id));
    }

    //add pinjaman transaction
    @Operation(summary = "Ajukan transaksi pinjaman baru", description = "Membuat pengajuan pinjaman baru untuk seorang customer (status awal \"Pengajuan\"). Bisa diakses oleh MARKETING, CUSTOMER, atau SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pengajuan pinjaman berhasil dibuat", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.getPinjamanTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "nominal pinjaman harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (MARKETING/CUSTOMER/SUPERADMIN saja)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Customer atau produk pinjaman tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Customer dengan id 5 tidak ditemukan")))
    })
    @PostMapping
    public ResponseEntity<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> addPinjamanTransaction(
            @Valid @RequestBody PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest request) {
        return ResponseEntity.ok(pinjamanTransactionService.addPinjamanTransaction(request));
    }

    //update pinjaman transaction
    @Operation(summary = "Update transaksi pinjaman (koreksi data master)", description = "Memperbarui seluruh field transaksi pinjaman secara langsung, dipakai untuk koreksi data manual, bukan alur kerja staff bertahap. Hanya bisa diakses oleh SUPERADMIN. Jika statusPengajuan diisi \"Disetujui\", nominal pinjaman divalidasi terhadap sisa plafond customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaksi pinjaman berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Pinjaman Transaction Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal, parameter id tidak dikirim, atau nominal pinjaman melebihi sisa plafond customer", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Nominal pinjaman melebihi sisa plafond customer (sisa plafond: 5000000)"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman, customer, produk pinjaman, atau employee tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @PutMapping
    public ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse> updatePinjamanTransaction(
            @RequestParam Integer id,
            @Valid @RequestBody PinjamanTransactionServiceRequest.pinjamanTransactionUpdateRequest request
    ) {
        pinjamanTransactionService.updatePinjamanTransaction(id, request.customerId, request.pinjamanId,
                request.nominalPinjaman, request.tenor, request.statusPengajuan, request.tanggalReview, request.tanggalApproval,
                request.noteMarketing, request.noteBm, request.noteBackOffice, request.lastUpdateBy);

        PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse respUpdate = new PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse();
        respUpdate.setMessage("Pinjaman Transaction Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    // tahap review oleh MARKETING: Pengajuan -> Direview
    @Operation(summary = "Review pengajuan pinjaman", description = "Tahap review oleh MARKETING: mengubah status transaksi dari \"Pengajuan\" menjadi \"Direview\". Membutuhkan role MARKETING atau SUPERADMIN, dan transaksi harus sedang berstatus \"Pengajuan\".")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pengajuan berhasil direview", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Pengajuan berhasil direview\"}"))),
            @ApiResponse(responseCode = "400", description = "Transaksi tidak sedang berstatus \"Pengajuan\"", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Transaksi harus berstatus 'Pengajuan' untuk bisa direview (status saat ini: Direview)"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya MARKETING/SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @PutMapping("/{id}/review")
    public ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse> reviewPinjamanTransaction(
            @PathVariable Integer id,
            @RequestBody PinjamanTransactionServiceRequest.pinjamanTransactionReviewRequest request
    ) {
        pinjamanTransactionService.reviewPinjamanTransaction(id, request.note);

        PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse resp = new PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse();
        resp.setMessage("Pengajuan berhasil direview");
        return ResponseEntity.ok(resp);
    }

    // tahap approval oleh BRANCH_MANAGER: Direview -> Disetujui/Ditolak
    @Operation(summary = "Approve/reject pengajuan pinjaman", description = "Tahap approval oleh BRANCH_MANAGER: mengubah status transaksi dari \"Direview\" menjadi \"Disetujui\" atau \"Ditolak\". Membutuhkan role BRANCH_MANAGER atau SUPERADMIN, transaksi harus berstatus \"Direview\", dan jika disetujui nominal pinjaman divalidasi terhadap sisa plafond customer. Menolak pengajuan akan mengirim push notification ke customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pengajuan berhasil disetujui/ditolak", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Pengajuan berhasil disetujui\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi gagal: field approved tidak dikirim, transaksi tidak berstatus \"Direview\", atau nominal pinjaman melebihi sisa plafond customer", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Nominal pinjaman melebihi sisa plafond customer (sisa plafond: 5000000)"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya BRANCH_MANAGER/SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @PutMapping("/{id}/approval")
    public ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse> approvalPinjamanTransaction(
            @PathVariable Integer id,
            @Valid @RequestBody PinjamanTransactionServiceRequest.pinjamanTransactionApprovalRequest request
    ) {
        pinjamanTransactionService.approvalPinjamanTransaction(id, request.approved, request.note);

        PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse resp = new PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse();
        resp.setMessage(request.approved ? "Pengajuan berhasil disetujui" : "Pengajuan berhasil ditolak");
        return ResponseEntity.ok(resp);
    }

    // tahap pencairan oleh BACKOFFICE: Disetujui -> Dicairkan
    @Operation(summary = "Cairkan pinjaman", description = "Tahap pencairan oleh BACKOFFICE: mengubah status transaksi dari \"Disetujui\" menjadi \"Dicairkan\" dan mengirim push notification ke customer. Membutuhkan role BACKOFFICE atau SUPERADMIN, dan transaksi harus berstatus \"Disetujui\".")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pinjaman berhasil dicairkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Pinjaman berhasil dicairkan\"}"))),
            @ApiResponse(responseCode = "400", description = "Transaksi tidak sedang berstatus \"Disetujui\"", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Transaksi harus berstatus 'Disetujui' untuk bisa dicairkan (status saat ini: Direview)"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya BACKOFFICE/SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id tidak ditemukan")))
    })
    @PutMapping("/{id}/disburse")
    public ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse> disbursePinjamanTransaction(
            @PathVariable Integer id,
            @RequestBody PinjamanTransactionServiceRequest.pinjamanTransactionDisburseRequest request
    ) {
        pinjamanTransactionService.disbursePinjamanTransaction(id, request.note);

        PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse resp = new PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse();
        resp.setMessage("Pinjaman berhasil dicairkan");
        return ResponseEntity.ok(resp);
    }

    //delete pinjaman transaction
    @Operation(summary = "Hapus transaksi pinjaman", description = "Menghapus data transaksi pinjaman berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaksi pinjaman berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete pinjaman transaction successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Transaksi pinjaman dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "pinjaman transaction id: 1 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse> deletePinjamanTransaction(@RequestParam Integer Id) {
        pinjamanTransactionService.deletePinjamanTransaction(Id);

        PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse respDelete = new PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse();
        respDelete.setMessage("Delete pinjaman transaction successfully");
        return ResponseEntity.ok(respDelete);
    }
}
