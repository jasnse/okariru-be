package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import com.project.binar.okariru.service.PinjamanTransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PinjamanTransactionControllerTest {

    @Mock
    private PinjamanTransactionService pinjamanTransactionService;

    @InjectMocks
    private PinjamanTransactionController controller;

    private PinjamanTransactionServiceResponse.getPinjamanTransactionResponse trx() {
        return new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(3, "TRX-2026-00003", 7, "andi", 1,
                null, null, null, 5_000_000, 12, "Pengajuan", null, null, null, null, null, "KTA");
    }

    @Test
    void findAll() {
        Page<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> page = new PageImpl<>(List.of(trx()));
        when(pinjamanTransactionService.findAll("Pengajuan", "trx", 0, 5)).thenReturn(page);

        ResponseEntity<Page<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse>> result =
                controller.findAll("Pengajuan", "trx", 0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void findByCustomerId() {
        when(pinjamanTransactionService.findByCustomerId(7, "Lunas", "trx")).thenReturn(List.of(trx()));

        assertEquals(1, controller.findByCustomerId(7, "Lunas", "trx").getBody().size());
    }

    @Test
    void getPinjamanTransactionById() {
        when(pinjamanTransactionService.getPinjamanTransactionById(3)).thenReturn(trx());

        assertEquals("TRX-2026-00003", controller.getPinjamanTransactionById(3).getBody().getKodeTransaksi());
    }

    @Test
    void addPinjamanTransaction() {
        PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest req =
                new PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest();
        when(pinjamanTransactionService.addPinjamanTransaction(req)).thenReturn(trx());

        assertEquals(3, controller.addPinjamanTransaction(req).getBody().getTransPinjamanId());
    }

    @Test
    void updatePinjamanTransaction_meneruskanSemuaFieldDanMengembalikanPesan() {
        PinjamanTransactionServiceRequest.pinjamanTransactionUpdateRequest req =
                new PinjamanTransactionServiceRequest.pinjamanTransactionUpdateRequest();
        req.customerId = 7;
        req.pinjamanId = 1;
        req.nominalPinjaman = 5_000_000;
        req.tenor = 12;
        req.statusPengajuan = "Direview";
        req.tanggalReview = LocalDate.of(2026, 1, 5);
        req.tanggalApproval = LocalDate.of(2026, 1, 10);
        req.noteMarketing = "m";
        req.noteBm = "bm";
        req.noteBackOffice = "bo";
        req.lastUpdateBy = 11;

        ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionUpdateResponse> result =
                controller.updatePinjamanTransaction(3, req);

        verify(pinjamanTransactionService).updatePinjamanTransaction(3, 7, 1, 5_000_000, 12, "Direview",
                LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 10), "m", "bm", "bo", 11);
        assertEquals("Pinjaman Transaction Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deletePinjamanTransaction() {
        ResponseEntity<PinjamanTransactionServiceResponse.pinjamanTransactionDeleteResponse> result =
                controller.deletePinjamanTransaction(3);

        verify(pinjamanTransactionService).deletePinjamanTransaction(3);
        assertEquals("Delete pinjaman transaction successfully", result.getBody().getMessage());
    }
}
