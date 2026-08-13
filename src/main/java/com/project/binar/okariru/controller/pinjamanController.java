package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PinjamanRequest;
import com.project.binar.okariru.dto.PinjamanResponse;
import com.project.binar.okariru.service.PinjamanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pinjaman")
@RequiredArgsConstructor
public class PinjamanController {
    private final PinjamanService pinjamanService;

    //get all pinjaman
    @GetMapping
    public ResponseEntity<List<PinjamanResponse.getPinjamanResponse>> findAll() {
        return ResponseEntity.ok(pinjamanService.getAllPinjaman());
    }

    //get pinjaman by Id
    @GetMapping(headers = "idPinjamanSearch")
    public ResponseEntity<PinjamanResponse.getPinjamanResponse> getPinjamanById(
            @Valid @RequestHeader("idPinjamanSearch") Integer id) {
        return ResponseEntity.ok(pinjamanService.getPinjamanById(id));
    }

    //add pinjaman
    @PostMapping
    public ResponseEntity<PinjamanResponse.getPinjamanResponse> addPinjaman(
            @Valid @RequestBody PinjamanRequest.pinjamanAddRequest request) {
        return ResponseEntity.ok(pinjamanService.addPinjaman(request));
    }

    //update pinjaman
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
    @DeleteMapping
    public ResponseEntity<PinjamanResponse.pinjamanDeleteResponse> deletePinjaman(
            @Valid @RequestParam Integer Id) {
        pinjamanService.deletePinjaman(Id);

        PinjamanResponse.pinjamanDeleteResponse respDelete = new PinjamanResponse.pinjamanDeleteResponse();
        respDelete.setMessage("Delete pinjaman successfully");
        return ResponseEntity.ok(respDelete);
    }
}
