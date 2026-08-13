package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import com.project.binar.okariru.service.PinjamanTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pinjaman/transaction")
@RequiredArgsConstructor
public class PinjamanTransactionController {
    private final PinjamanTransactionService pinjamanTransactionService;

    //get all pinjaman transaction
    @GetMapping
    public ResponseEntity<List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse>> findAll() {
        return ResponseEntity.ok(pinjamanTransactionService.getAllPinjamanTransaction());
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
                request.nominalPinjaman, request.statusPengajuan, request.tanggalReview, request.tanggalApproval,
                request.noteApproval, request.rejectNote, request.lastUpdateBy);

        PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse respUpdate = new PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse();
        respUpdate.setMessage("Pinjaman Transaction Berhasil di update");
        return ResponseEntity.ok(respUpdate);
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
