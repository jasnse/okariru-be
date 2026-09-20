package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PinjamanRequest;
import com.project.binar.okariru.dto.PinjamanResponse;
import com.project.binar.okariru.service.PinjamanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PinjamanControllerTest {

    @Mock
    private PinjamanService pinjamanService;

    @InjectMocks
    private PinjamanController controller;

    private PinjamanResponse.getPinjamanResponse pinjaman() {
        return new PinjamanResponse.getPinjamanResponse(1, "KTA", "desk", 1.5, 50_000.0, null, null);
    }

    @Test
    void findAll() {
        Page<PinjamanResponse.getPinjamanResponse> page = new PageImpl<>(List.of(pinjaman()));
        when(pinjamanService.findAll("kta", 0, 5)).thenReturn(page);

        ResponseEntity<Page<PinjamanResponse.getPinjamanResponse>> result = controller.findAll("kta", 0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void getPinjamanById() {
        when(pinjamanService.getPinjamanById(1)).thenReturn(pinjaman());

        assertEquals("KTA", controller.getPinjamanById(1).getBody().getJenisPinjaman());
    }

    @Test
    void addPinjaman() {
        PinjamanRequest.pinjamanAddRequest req = new PinjamanRequest.pinjamanAddRequest();
        when(pinjamanService.addPinjaman(req)).thenReturn(pinjaman());

        assertEquals(1, controller.addPinjaman(req).getBody().getPinjamanId());
    }

    @Test
    void updatePinjaman_memanggilServiceDanMengembalikanPesan() {
        PinjamanRequest.pinjamanUpdateRequest req = new PinjamanRequest.pinjamanUpdateRequest();
        req.jenisPinjaman = "KPR";
        req.deskripsiPinjaman = "rumah";
        req.bunga = 2.0;
        req.biayaLainnya = 100.0;

        ResponseEntity<PinjamanResponse.pinjamanUpdateResponse> result = controller.updatePinjaman(1, req);

        verify(pinjamanService).updatePinjaman(1, "KPR", "rumah", 2.0, 100.0);
        assertEquals("Pinjaman Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deletePinjaman_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<PinjamanResponse.pinjamanDeleteResponse> result = controller.deletePinjaman(1);

        verify(pinjamanService).deletePinjaman(1);
        assertEquals("Delete pinjaman successfully", result.getBody().getMessage());
    }
}
