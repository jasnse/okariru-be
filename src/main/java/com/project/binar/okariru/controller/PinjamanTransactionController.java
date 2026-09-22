package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import com.project.binar.okariru.service.PinjamanTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/customer")
    public ResponseEntity<List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse>> findByCustomerId(
            @RequestParam Integer customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ){
        return ResponseEntity.ok(pinjamanTransactionService.findByCustomerId(customerId, status, keyword));
    }

    //get pinjaman transaction by Id
    @GetMapping(headers = "idPinjamanTransactionSearch")
    public ResponseEntity<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> getPinjamanTransactionById(
            @RequestHeader("idPinjamanTransactionSearch") Integer id) {
        return ResponseEntity.ok(pinjamanTransactionService.getPinjamanTransactionById(id));
    }

    //add pinjaman transaction
    @PostMapping
    public ResponseEntity<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> addPinjamanTransaction(
            @Valid @RequestBody PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest request) {
        return ResponseEntity.ok(pinjamanTransactionService.addPinjamanTransaction(request));
    }

    //update pinjaman transaction
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
    @DeleteMapping
    public ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse> deletePinjamanTransaction(@RequestParam Integer Id) {
        pinjamanTransactionService.deletePinjamanTransaction(Id);

        PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse respDelete = new PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse();
        respDelete.setMessage("Delete pinjaman transaction successfully");
        return ResponseEntity.ok(respDelete);
    }
}
