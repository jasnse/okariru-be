package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.AngsuranRequest;
import com.project.binar.okariru.dto.AngsuranResponse;
import com.project.binar.okariru.entity.AngsuranEntity;
import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.repository.AngsuranRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AngsuranServiceImplTest {

    @Mock
    private AngsuranRepository angsuranRepository;
    @Mock
    private PinjamanTransactionRepository pinjamanTransactionRepository;

    @InjectMocks
    private AngsuranServiceImpl service;

    private PinjamanTransactionEntity trx;
    private PinjamanEntity produk;

    @BeforeEach
    void setUp() {
        produk = new PinjamanEntity();
        produk.setPinjamanId(1);
        produk.setBunga(1.0);
        produk.setBiayaLainnya(30_000.0);

        trx = new PinjamanTransactionEntity();
        trx.setTransPinjamanId(3);
        trx.setPinjaman(produk);
        trx.setNominalPinjaman(1_200_000);
        trx.setTanggalApproval(LocalDate.of(2026, 1, 15));
        trx.setStatusPengajuan("Dicairkan");
    }

    private AngsuranEntity angsuran(int id, int tenor, int sisa) {
        AngsuranEntity a = new AngsuranEntity();
        a.setAngsuranId(id);
        a.setTransPinjaman(trx);
        a.setJumlahPokok(100L);
        a.setJumlahBunga(1.0);
        a.setTotalAngsuran(100);
        a.setTanggalJatuhTempo(LocalDate.of(2026, 2, 15).plusMonths(tenor));
        a.setStatusAngsuran("Belum Bayar");
        a.setTenor(tenor);
        a.setSisaTagihan(sisa);
        return a;
    }

    private AngsuranRequest.angsuranGenerateRequest generateRequest(int tenor) {
        AngsuranRequest.angsuranGenerateRequest req = new AngsuranRequest.angsuranGenerateRequest();
        req.transPinjamanId = 3;
        req.tenor = tenor;
        return req;
    }

    private AngsuranRequest.angsuranBayarRequest bayarRequest(int nominal) {
        AngsuranRequest.angsuranBayarRequest req = new AngsuranRequest.angsuranBayarRequest();
        req.transPinjamanId = 3;
        req.nominalBayar = nominal;
        return req;
    }

    // ---------- read ----------

    @Test
    void getAllAngsuran_memetakanSemua() {
        when(angsuranRepository.findAll()).thenReturn(List.of(angsuran(1, 1, 100)));

        List<AngsuranResponse.getAngsuranResponse> result = service.getAllAngsuran();

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getTransPinjamanId());
        assertEquals(100, result.get(0).getSisaTagihan());
    }

    @Test
    void getAngsuranById_ditemukan() {
        when(angsuranRepository.findById(1)).thenReturn(Optional.of(angsuran(1, 1, 100)));

        assertEquals(1, service.getAngsuranById(1).getAngsuranId());
    }

    @Test
    void getAngsuranById_tidakDitemukan() {
        when(angsuranRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getAngsuranById(1));
    }

    @Test
    void getAngsuranByTransPinjaman_berhasil() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.findByTransPinjamanOrderByTenorAsc(trx))
                .thenReturn(List.of(angsuran(1, 1, 100), angsuran(2, 2, 100)));

        List<AngsuranResponse.getAngsuranResponse> result = service.getAngsuranByTransPinjaman(3);

        assertEquals(2, result.size());
        assertEquals(2, result.get(1).getTenor());
    }

    @Test
    void getAngsuranByTransPinjaman_transaksiTidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getAngsuranByTransPinjaman(3));
    }

    // ---------- addAngsuran ----------

    private AngsuranRequest.angsuranAddRequest addRequest() {
        AngsuranRequest.angsuranAddRequest req = new AngsuranRequest.angsuranAddRequest();
        req.transPinjamanId = 3;
        req.jumlahPokok = 400_000L;
        req.jumlahBunga = 12_000.0;
        req.totalAngsuran = 422_000;
        req.tanggalJatuhTempo = LocalDate.of(2026, 2, 15);
        req.statusAngsuran = "Belum Bayar";
        req.tenor = 1;
        return req;
    }

    @Test
    void addAngsuran_berhasil_sisaTagihanSamaDenganTotal() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.save(any(AngsuranEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        AngsuranResponse.getAngsuranResponse r = service.addAngsuran(addRequest());

        assertEquals(422_000, r.getTotalAngsuran());
        assertEquals(422_000, r.getSisaTagihan());
        assertEquals(3, r.getTransPinjamanId());
    }

    @Test
    void addAngsuran_transaksiTidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addAngsuran(addRequest()));
        verify(angsuranRepository, never()).save(any());
    }

    // ---------- generateAngsuran ----------

    @Test
    @SuppressWarnings("unchecked")
    void generateAngsuran_menghitungPokokBungaDanJatuhTempo() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<AngsuranResponse.getAngsuranResponse> result = service.generateAngsuran(generateRequest(3));

        assertEquals(3, result.size());
        // pokok 1.200.000/3 = 400.000 ; bunga 1% x 1.200.000 = 12.000 ; biaya 30.000/3 = 10.000
        AngsuranResponse.getAngsuranResponse pertama = result.get(0);
        assertEquals(400_000L, pertama.getJumlahPokok());
        assertEquals(12_000.0, pertama.getJumlahBunga());
        assertEquals(422_000, pertama.getTotalAngsuran());
        assertEquals(422_000, pertama.getSisaTagihan());
        assertEquals("Belum Bayar", pertama.getStatusAngsuran());
        assertEquals(1, pertama.getTenor());
        assertEquals(LocalDate.of(2026, 2, 15), pertama.getTanggalJatuhTempo());
        assertEquals(LocalDate.of(2026, 4, 15), result.get(2).getTanggalJatuhTempo());
        assertEquals(3, result.get(2).getTenor());

        ArgumentCaptor<List<AngsuranEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(angsuranRepository).saveAll(captor.capture());
        assertEquals(3, captor.getValue().size());
    }

    @Test
    void generateAngsuran_bungaDanBiayaNull_dianggapNol() {
        produk.setBunga(null);
        produk.setBiayaLainnya(null);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<AngsuranResponse.getAngsuranResponse> result = service.generateAngsuran(generateRequest(4));

        assertEquals(300_000, result.get(0).getTotalAngsuran());
        assertEquals(0.0, result.get(0).getJumlahBunga());
    }

    @Test
    void generateAngsuran_tanpaTanggalApproval_memakaiHariIni() {
        trx.setTanggalApproval(null);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<AngsuranResponse.getAngsuranResponse> result = service.generateAngsuran(generateRequest(2));

        assertEquals(LocalDate.now().plusMonths(1), result.get(0).getTanggalJatuhTempo());
    }

    @Test
    void generateAngsuran_transaksiTidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.generateAngsuran(generateRequest(3)));
    }

    @Test
    void generateAngsuran_tanpaProdukPinjaman() {
        trx.setPinjaman(null);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        assertThrows(EntityNotFoundException.class, () -> service.generateAngsuran(generateRequest(3)));
        verify(angsuranRepository, never()).saveAll(any());
    }

    @Test
    void generateAngsuran_tanpaNominalPinjaman() {
        trx.setNominalPinjaman(null);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        assertThrows(EntityNotFoundException.class, () -> service.generateAngsuran(generateRequest(3)));
        verify(angsuranRepository, never()).saveAll(any());
    }

    // ---------- bayarAngsuran ----------

    @Test
    void bayarAngsuran_transaksiTidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.bayarAngsuran(bayarRequest(100)));
    }

    @Test
    void bayarAngsuran_belumAdaAngsuran() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.findByTransPinjamanOrderByTenorAsc(trx)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> service.bayarAngsuran(bayarRequest(100)));
    }

    @Test
    void bayarAngsuran_melebihiTotalSisaTagihan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.findByTransPinjamanOrderByTenorAsc(trx))
                .thenReturn(List.of(angsuran(1, 1, 100), angsuran(2, 2, 100)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.bayarAngsuran(bayarRequest(201)));
        assertTrue(ex.getMessage().contains("200"));
        verify(angsuranRepository, never()).save(any());
    }

    @Test
    void bayarAngsuran_sebagian_pertamaLunasKeduaKurangBayarKetigaUtuh() {
        AngsuranEntity a1 = angsuran(1, 1, 100);
        AngsuranEntity a2 = angsuran(2, 2, 100);
        AngsuranEntity a3 = angsuran(3, 3, 100);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.findByTransPinjamanOrderByTenorAsc(trx)).thenReturn(List.of(a1, a2, a3));

        AngsuranResponse.angsuranBayarResponse resp = service.bayarAngsuran(bayarRequest(150));

        assertEquals(0, a1.getSisaTagihan());
        assertEquals("Lunas", a1.getStatusAngsuran());
        assertEquals(50, a2.getSisaTagihan());
        assertEquals("Kurang Bayar", a2.getStatusAngsuran());
        assertEquals(100, a3.getSisaTagihan());
        assertEquals("Belum Bayar", a3.getStatusAngsuran());
        assertTrue(resp.getMessage().contains("1 angsuran Lunas"));
        assertTrue(resp.getMessage().contains("150"));
        verify(angsuranRepository).save(a1);
        verify(angsuranRepository).save(a2);
        verify(angsuranRepository, never()).save(a3);
        verify(pinjamanTransactionRepository, never()).save(any());
        assertEquals("Dicairkan", trx.getStatusPengajuan());
    }

    @Test
    void bayarAngsuran_semuaLunas_statusTransaksiJadiLunas() {
        AngsuranEntity a1 = angsuran(1, 1, 100);
        AngsuranEntity a2 = angsuran(2, 2, 100);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.findByTransPinjamanOrderByTenorAsc(trx)).thenReturn(List.of(a1, a2));

        AngsuranResponse.angsuranBayarResponse resp = service.bayarAngsuran(bayarRequest(200));

        assertEquals("Lunas", a1.getStatusAngsuran());
        assertEquals("Lunas", a2.getStatusAngsuran());
        assertEquals("Lunas", trx.getStatusPengajuan());
        verify(pinjamanTransactionRepository).save(trx);
        assertTrue(resp.getMessage().contains("2 angsuran Lunas"));
    }

    @Test
    void bayarAngsuran_melewatiAngsuranYangSudahLunas() {
        AngsuranEntity a1 = angsuran(1, 1, 0);
        a1.setStatusAngsuran("Lunas");
        AngsuranEntity a2 = angsuran(2, 2, 100);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.findByTransPinjamanOrderByTenorAsc(trx)).thenReturn(List.of(a1, a2));

        service.bayarAngsuran(bayarRequest(100));

        verify(angsuranRepository, never()).save(a1);
        verify(angsuranRepository).save(a2);
        assertEquals("Lunas", a2.getStatusAngsuran());
        assertEquals("Lunas", trx.getStatusPengajuan());
    }

    @Test
    void bayarAngsuran_nominalNol_tidakMengubahApaPun() {
        AngsuranEntity a1 = angsuran(1, 1, 100);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(angsuranRepository.findByTransPinjamanOrderByTenorAsc(trx)).thenReturn(List.of(a1));

        AngsuranResponse.angsuranBayarResponse resp = service.bayarAngsuran(bayarRequest(0));

        assertEquals(100, a1.getSisaTagihan());
        assertTrue(resp.getMessage().contains("0 angsuran Lunas"));
        verify(angsuranRepository, never()).save(any());
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    // ---------- updateAngsuran ----------

    @Test
    void updateAngsuran_berhasil() {
        AngsuranEntity a = angsuran(1, 1, 100);
        when(angsuranRepository.findById(1)).thenReturn(Optional.of(a));
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        service.updateAngsuran(1, 3, 500L, 5.0, 505, LocalDate.of(2026, 5, 1), "Lunas", 2);

        assertEquals(500L, a.getJumlahPokok());
        assertEquals(5.0, a.getJumlahBunga());
        assertEquals(505, a.getTotalAngsuran());
        assertEquals("Lunas", a.getStatusAngsuran());
        assertEquals(2, a.getTenor());
        verify(angsuranRepository).save(a);
    }

    @Test
    void updateAngsuran_angsuranTidakDitemukan() {
        when(angsuranRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.updateAngsuran(1, 3, 1L, 1.0, 1, LocalDate.now(), "x", 1));
    }

    @Test
    void updateAngsuran_transaksiTidakDitemukan() {
        when(angsuranRepository.findById(1)).thenReturn(Optional.of(angsuran(1, 1, 100)));
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.updateAngsuran(1, 3, 1L, 1.0, 1, LocalDate.now(), "x", 1));
        verify(angsuranRepository, never()).save(any());
    }

    // ---------- deleteAngsuran ----------

    @Test
    void deleteAngsuran_berhasil() {
        AngsuranEntity a = angsuran(1, 1, 100);
        when(angsuranRepository.findById(1)).thenReturn(Optional.of(a));

        String msg = service.deleteAngsuran(1);

        assertTrue(msg.contains("1"));
        verify(angsuranRepository).delete(a);
    }

    @Test
    void deleteAngsuran_tidakDitemukan() {
        when(angsuranRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteAngsuran(1));
    }
}
