package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.AngsuranRequest;
import com.project.binar.okariru.dto.AngsuranResponse;
import com.project.binar.okariru.service.AngsuranService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/angsuran")
@RequiredArgsConstructor
public class AngsuranController {
    private final AngsuranService angsuranService;

    //get all angsuran
    @GetMapping
    public ResponseEntity<List<AngsuranResponse.getAngsuranResponse>> findAll() {
        return ResponseEntity.ok(angsuranService.getAllAngsuran());
    }

    //get angsuran by Id
    @GetMapping(headers = "idAngsuranSearch")
    public ResponseEntity<AngsuranResponse.getAngsuranResponse> getAngsuranById(
            @RequestHeader("idAngsuranSearch") Integer id) {
        return ResponseEntity.ok(angsuranService.getAngsuranById(id));
    }

    //add angsuran
    @PostMapping
    public ResponseEntity<AngsuranResponse.getAngsuranResponse> addAngsuran(
            @Valid @RequestBody AngsuranRequest.angsuranAddRequest request) {
        return ResponseEntity.ok(angsuranService.addAngsuran(request));
    }

    //generate angsuran otomatis sesuai tenor
    @PostMapping("/generate")
    public ResponseEntity<List<AngsuranResponse.getAngsuranResponse>> generateAngsuran(
            @Valid @RequestBody AngsuranRequest.angsuranGenerateRequest request) {
        return ResponseEntity.ok(angsuranService.generateAngsuran(request));
    }

    //bayar angsuran (otomatis dialokasikan ke semua angsuran yang belum lunas, urut tenor)
    @PostMapping("/bayar")
    public ResponseEntity<AngsuranResponse.angsuranBayarResponse> bayarAngsuran(
            @Valid @RequestBody AngsuranRequest.angsuranBayarRequest request
    ) {
        return ResponseEntity.ok(angsuranService.bayarAngsuran(request));
    }

    //update angsuran
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
    @DeleteMapping
    public ResponseEntity<AngsuranResponse.angsuranDeleteResponse> deleteAngsuran(@RequestParam Integer Id) {
            angsuranService.deleteAngsuran(Id);

        AngsuranResponse.angsuranDeleteResponse respDelete = new AngsuranResponse.angsuranDeleteResponse();
        respDelete.setMessage("Delete angsuran successfully");
        return ResponseEntity.ok(respDelete);
    }
}
