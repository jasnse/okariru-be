package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.PinjamanRequest;
import com.project.binar.okariru.dto.PinjamanResponse;
import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.repository.PinjamanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PinjamanServiceImplTest {

    @Mock
    private PinjamanRepository pinjamanRepository;

    @InjectMocks
    private PinjamanServiceImpl service;

    private PinjamanEntity pinjaman;

    @BeforeEach
    void setUp() {
        pinjaman = new PinjamanEntity();
        pinjaman.setPinjamanId(1);
        pinjaman.setJenisPinjaman("KTA");
        pinjaman.setDeskripsiPinjaman("Kredit tanpa agunan");
        pinjaman.setBunga(1.5);
        pinjaman.setBiayaLainnya(50_000.0);
        pinjaman.setCreatedAt(LocalDate.of(2026, 1, 1));
    }

    private PinjamanRequest.pinjamanAddRequest addRequest() {
        PinjamanRequest.pinjamanAddRequest req = new PinjamanRequest.pinjamanAddRequest();
        req.jenisPinjaman = "KTA";
        req.deskripsiPinjaman = "Kredit tanpa agunan";
        req.bunga = 1.5;
        req.biayaLainnya = 50_000.0;
        return req;
    }

    @Test
    void getAllPinjaman_memetakanSemua() {
        when(pinjamanRepository.findAll()).thenReturn(List.of(pinjaman));

        List<PinjamanResponse.getPinjamanResponse> result = service.getAllPinjaman();

        assertEquals(1, result.size());
        assertEquals("KTA", result.get(0).getJenisPinjaman());
        assertEquals(1.5, result.get(0).getBunga());
    }

    @Test
    void findAll_memetakanPageDanMengurutkanBerdasarkanPinjamanId() {
        when(pinjamanRepository.searchPinjaman(eq("kta"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pinjaman)));

        Page<PinjamanResponse.getPinjamanResponse> result = service.findAll("kta", 0, 5);

        assertEquals(1, result.getTotalElements());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(pinjamanRepository).searchPinjaman(eq("kta"), captor.capture());
        assertNotNull(captor.getValue().getSort().getOrderFor("pinjamanId"));
    }

    @Test
    void getPinjamanById_ditemukan() {
        when(pinjamanRepository.findById(1)).thenReturn(Optional.of(pinjaman));

        assertEquals(50_000.0, service.getPinjamanById(1).getBiayaLainnya());
    }

    @Test
    void getPinjamanById_tidakDitemukan() {
        when(pinjamanRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getPinjamanById(1));
    }

    @Test
    void addPinjaman_berhasil() {
        when(pinjamanRepository.existsByJenisPinjaman("KTA")).thenReturn(false);
        when(pinjamanRepository.save(any(PinjamanEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        PinjamanResponse.getPinjamanResponse r = service.addPinjaman(addRequest());

        assertEquals("KTA", r.getJenisPinjaman());
        assertEquals(LocalDate.now(), r.getCreatedAt());
    }

    @Test
    void addPinjaman_jenisSudahAda() {
        when(pinjamanRepository.existsByJenisPinjaman("KTA")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.addPinjaman(addRequest()));
        verify(pinjamanRepository, never()).save(any());
    }

    @Test
    void updatePinjaman_berhasil() {
        when(pinjamanRepository.findById(1)).thenReturn(Optional.of(pinjaman));
        when(pinjamanRepository.existsByJenisPinjamanAndPinjamanIdNot("KPR", 1)).thenReturn(false);

        service.updatePinjaman(1, "KPR", "Kredit rumah", 2.0, 100_000.0);

        assertEquals("KPR", pinjaman.getJenisPinjaman());
        assertEquals("Kredit rumah", pinjaman.getDeskripsiPinjaman());
        assertEquals(2.0, pinjaman.getBunga());
        assertEquals(100_000.0, pinjaman.getBiayaLainnya());
        assertEquals(LocalDate.now(), pinjaman.getUpdatedAt());
        verify(pinjamanRepository).save(pinjaman);
    }

    @Test
    void updatePinjaman_tidakDitemukan() {
        when(pinjamanRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updatePinjaman(1, "a", "b", 1.0, 1.0));
    }

    @Test
    void updatePinjaman_jenisSudahDipakaiLain() {
        when(pinjamanRepository.findById(1)).thenReturn(Optional.of(pinjaman));
        when(pinjamanRepository.existsByJenisPinjamanAndPinjamanIdNot("KPR", 1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.updatePinjaman(1, "KPR", "b", 1.0, 1.0));
        verify(pinjamanRepository, never()).save(any());
    }

    @Test
    void deletePinjaman_berhasil() {
        when(pinjamanRepository.findById(1)).thenReturn(Optional.of(pinjaman));

        String msg = service.deletePinjaman(1);

        assertTrue(msg.contains("1"));
        verify(pinjamanRepository).delete(pinjaman);
    }

    @Test
    void deletePinjaman_tidakDitemukan() {
        when(pinjamanRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deletePinjaman(1));
        verify(pinjamanRepository, never()).delete(any());
    }
}
