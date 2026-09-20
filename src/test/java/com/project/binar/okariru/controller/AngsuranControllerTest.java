package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.AngsuranRequest;
import com.project.binar.okariru.dto.AngsuranResponse;
import com.project.binar.okariru.service.AngsuranService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AngsuranControllerTest {

    @Mock
    private AngsuranService angsuranService;

    @InjectMocks
    private AngsuranController controller;

    private AngsuranResponse.getAngsuranResponse angsuran() {
        return new AngsuranResponse.getAngsuranResponse(1, 3, 100L, 1.0, 101, null, "Belum Bayar", 1, 101);
    }

    @Test
    void findAll() {
        when(angsuranService.getAllAngsuran()).thenReturn(List.of(angsuran()));

        ResponseEntity<List<AngsuranResponse.getAngsuranResponse>> result = controller.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getAngsuranById() {
        when(angsuranService.getAngsuranById(1)).thenReturn(angsuran());

        assertEquals(1, controller.getAngsuranById(1).getBody().getAngsuranId());
    }

    @Test
    void getAngsuranByTransPinjaman() {
        when(angsuranService.getAngsuranByTransPinjaman(3)).thenReturn(List.of(angsuran()));

        assertEquals(1, controller.getAngsuranByTransPinjaman(3).getBody().size());
    }

    @Test
    void addAngsuran() {
        AngsuranRequest.angsuranAddRequest req = new AngsuranRequest.angsuranAddRequest();
        when(angsuranService.addAngsuran(req)).thenReturn(angsuran());

        assertEquals(101, controller.addAngsuran(req).getBody().getTotalAngsuran());
    }

    @Test
    void generateAngsuran() {
        AngsuranRequest.angsuranGenerateRequest req = new AngsuranRequest.angsuranGenerateRequest();
        when(angsuranService.generateAngsuran(req)).thenReturn(List.of(angsuran(), angsuran()));

        assertEquals(2, controller.generateAngsuran(req).getBody().size());
    }

    @Test
    void bayarAngsuran() {
        AngsuranRequest.angsuranBayarRequest req = new AngsuranRequest.angsuranBayarRequest();
        AngsuranResponse.angsuranBayarResponse resp = new AngsuranResponse.angsuranBayarResponse();
        resp.setMessage("Pembayaran berhasil");
        when(angsuranService.bayarAngsuran(req)).thenReturn(resp);

        assertEquals("Pembayaran berhasil", controller.bayarAngsuran(req).getBody().getMessage());
    }

    @Test
    void updateAngsuran_memanggilServiceDanMengembalikanPesan() {
        AngsuranRequest.angsuranUpdateRequest req = new AngsuranRequest.angsuranUpdateRequest();
        req.transPinjamanId = 3;
        req.jumlahPokok = 100L;
        req.jumlahBunga = 1.0;
        req.totalAngsuran = 101;
        req.tanggalJatuhTempo = LocalDate.of(2026, 2, 1);
        req.statusAngsuran = "Lunas";
        req.tenor = 1;

        ResponseEntity<AngsuranResponse.angsuranUpdateResponse> result = controller.updateAngsuran(1, req);

        verify(angsuranService).updateAngsuran(1, 3, 100L, 1.0, 101, LocalDate.of(2026, 2, 1), "Lunas", 1);
        assertEquals("Angsuran Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deleteAngsuran_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<AngsuranResponse.angsuranDeleteResponse> result = controller.deleteAngsuran(1);

        verify(angsuranService).deleteAngsuran(1);
        assertEquals("Delete angsuran successfully", result.getBody().getMessage());
    }
}
