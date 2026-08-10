package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.pinjamanRequest;
import com.project.binar.okariru.dto.pinjamanResponse;
import com.project.binar.okariru.service.pinjamanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pinjaman")
@RequiredArgsConstructor
public class pinjamanController {
    private final pinjamanService pinjamanService;

    //get all pinjaman
    @GetMapping
    public ResponseEntity<List<pinjamanResponse.getPinjamanResponse>> findAll() {
        return ResponseEntity.ok(pinjamanService.getAllPinjaman());
    }

    //get pinjaman by Id
    @GetMapping(headers = "idPinjamanSearch")
    public ResponseEntity<pinjamanResponse.getPinjamanResponse> getPinjamanById(
            @Valid @RequestHeader("idPinjamanSearch") Integer id) {
        return ResponseEntity.ok(pinjamanService.getPinjamanById(id));
    }

    //add pinjaman
    @PostMapping
    public ResponseEntity<pinjamanResponse.getPinjamanResponse> addPinjaman(
            @Valid @RequestBody pinjamanRequest.pinjamanAddRequest request) {
        return ResponseEntity.ok(pinjamanService.addPinjaman(request));
    }

    //update pinjaman
    @PutMapping
    public ResponseEntity<pinjamanResponse.pinjamanUpdateResponse> updatePinjaman(
            @Valid
            @RequestParam Integer id,
            @Valid @RequestBody pinjamanRequest.pinjamanUpdateRequest request
    ) {
        pinjamanService.updatePinjaman(id, request.jenisPinjaman, request.deskripsiPinjaman, request.bunga, request.biayaLainnya);

        pinjamanResponse.pinjamanUpdateResponse respUpdate = new pinjamanResponse.pinjamanUpdateResponse();
        respUpdate.setMessage("Pinjaman Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete pinjaman
    @DeleteMapping
    public ResponseEntity<pinjamanResponse.pinjamanDeleteResponse> deletePinjaman(
            @Valid @RequestParam Integer Id) {
        pinjamanService.deletePinjaman(Id);

        pinjamanResponse.pinjamanDeleteResponse respDelete = new pinjamanResponse.pinjamanDeleteResponse();
        respDelete.setMessage("Delete pinjaman successfully");
        return ResponseEntity.ok(respDelete);
    }
}
