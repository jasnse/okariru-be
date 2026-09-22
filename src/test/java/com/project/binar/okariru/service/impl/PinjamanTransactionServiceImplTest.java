package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import com.project.binar.okariru.entity.AppUser;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.entity.PlafondEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.PinjamanRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import com.project.binar.okariru.repository.PlafondRepository;
import com.project.binar.okariru.service.PushNotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PinjamanTransactionServiceImplTest {

    @Mock
    private PinjamanTransactionRepository pinjamanTransactionRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PinjamanRepository pinjamanRepository;
    @Mock
    private EmployeRepository employeRepository;
    @Mock
    private PlafondRepository plafondRepository;
    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private PinjamanTransactionServiceImpl service;

    private CustomerEntity customer;
    private PinjamanEntity produk;
    private EmployeEntity employee;
    private PinjamanTransactionEntity trx;

    @BeforeEach
    void setUp() {
        customer = new CustomerEntity();
        customer.setCustomerId(7);
        customer.setUserName("andi");

        produk = new PinjamanEntity();
        produk.setPinjamanId(1);
        produk.setJenisPinjaman("KTA");

        employee = new EmployeEntity();
        employee.setEmployeeId(11);

        trx = new PinjamanTransactionEntity();
        trx.setTransPinjamanId(3);
        trx.setKodeTransaksi("TRX-2026-00003");
        trx.setCustomer(customer);
        trx.setPinjaman(produk);
        trx.setNominalPinjaman(5_000_000);
        trx.setTenor(12);
        trx.setStatusPengajuan("Pengajuan");
        trx.setTanggalPengajuan(LocalDate.of(2026, 1, 1));
        trx.setLastUpdateBy(employee);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(Object principal) {
        SecurityContextHolder.setContext(
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken(principal, null, List.of())));
    }

    private void loginWithRole(String role) {
        loginAs(new AppUser("budi", "pw", role, null));
    }

    private PlafondEntity plafond(Integer total) {
        PlafondEntity p = new PlafondEntity();
        p.setUser(customer);
        p.setTotalPlafond(total);
        return p;
    }

    // ---------- findAll ----------

    @Test
    void findAll_memetakanPageLengkap() {
        when(pinjamanTransactionRepository.searchPinjamanTrx(eq("Pengajuan"), eq("trx"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(trx)));

        Page<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> result =
                service.findAll("Pengajuan", "trx", 0, 5);

        PinjamanTransactionServiceResponse.getPinjamanTransactionResponse r = result.getContent().get(0);
        assertEquals(3, r.getTransPinjamanId());
        assertEquals("TRX-2026-00003", r.getKodeTransaksi());
        assertEquals("andi", r.getCustomerName());
        assertEquals(1, r.getPinjamanId());
        assertEquals("KTA", r.getJenisPinjaman());
        assertEquals(11, r.getLastUpdateBy());
    }

    @Test
    void findAll_tanpaPinjamanDanLastUpdateBy_fieldNull() {
        trx.setPinjaman(null);
        trx.setLastUpdateBy(null);
        when(pinjamanTransactionRepository.searchPinjamanTrx(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(trx)));

        PinjamanTransactionServiceResponse.getPinjamanTransactionResponse r =
                service.findAll(null, null, 0, 5).getContent().get(0);

        assertNull(r.getPinjamanId());
        assertNull(r.getJenisPinjaman());
        assertNull(r.getLastUpdateBy());
    }

    // ---------- findByCustomerId ----------

    @Test
    void findByCustomerId_statusDanKeywordKosong_dinormalisasiJadiNull() {
        when(pinjamanTransactionRepository.findByCustomer(7, null, null)).thenReturn(List.of(trx));

        List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> result =
                service.findByCustomerId(7, "  ", "");

        assertEquals(1, result.size());
        verify(pinjamanTransactionRepository).findByCustomer(7, null, null);
    }

    @Test
    void findByCustomerId_statusDanKeywordDiteruskan() {
        trx.setPinjaman(null);
        trx.setLastUpdateBy(null);
        when(pinjamanTransactionRepository.findByCustomer(7, "Lunas", "trx")).thenReturn(List.of(trx));

        List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> result =
                service.findByCustomerId(7, "Lunas", "trx");

        assertNull(result.get(0).getPinjamanId());
    }

    // ---------- getPinjamanTransactionById ----------

    @Test
    void getPinjamanTransactionById_ditemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        assertEquals("KTA", service.getPinjamanTransactionById(3).getJenisPinjaman());
    }

    @Test
    void getPinjamanTransactionById_tanpaPinjamanDanLastUpdateBy() {
        trx.setPinjaman(null);
        trx.setLastUpdateBy(null);
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        PinjamanTransactionServiceResponse.getPinjamanTransactionResponse r = service.getPinjamanTransactionById(3);

        assertNull(r.getPinjamanId());
        assertNull(r.getLastUpdateBy());
    }

    @Test
    void getPinjamanTransactionById_tidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getPinjamanTransactionById(3));
    }

    // ---------- addPinjamanTransaction ----------

    private PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest addRequest(Integer pinjamanId, int nominal) {
        PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest req =
                new PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest();
        req.customerId = 7;
        req.pinjamanId = pinjamanId;
        req.nominalPinjaman = nominal;
        req.tenor = 12;
        return req;
    }

    private void stubSaveDenganId(int id) {
        when(pinjamanTransactionRepository.save(any(PinjamanTransactionEntity.class))).thenAnswer(inv -> {
            PinjamanTransactionEntity e = inv.getArgument(0);
            e.setTransPinjamanId(id);
            return e;
        });
    }

    @Test
    void addPinjamanTransaction_berhasil_membuatKodeTransaksi() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(10_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(2_000_000L);
        when(pinjamanRepository.findById(1)).thenReturn(Optional.of(produk));
        stubSaveDenganId(3);

        PinjamanTransactionServiceResponse.getPinjamanTransactionResponse r =
                service.addPinjamanTransaction(addRequest(1, 5_000_000));

        assertEquals("Pengajuan", r.getStatusPengajuan());
        assertEquals(LocalDate.now(), r.getTanggalPengajuan());
        assertEquals(String.format("TRX-%d-00003", LocalDate.now().getYear()), r.getKodeTransaksi());
        assertEquals("KTA", r.getJenisPinjaman());
        verify(pinjamanTransactionRepository, times(2)).save(any(PinjamanTransactionEntity.class));
    }

    @Test
    void addPinjamanTransaction_tanpaPinjamanId_tidakMemanggilPinjamanRepository() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.empty());
        stubSaveDenganId(4);

        PinjamanTransactionServiceResponse.getPinjamanTransactionResponse r =
                service.addPinjamanTransaction(addRequest(null, 5_000_000));

        assertNull(r.getPinjamanId());
        verifyNoInteractions(pinjamanRepository);
    }

    @Test
    void addPinjamanTransaction_customerTidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addPinjamanTransaction(addRequest(1, 1)));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    @Test
    void addPinjamanTransaction_melebihiSisaPlafond() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(10_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(8_000_000L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addPinjamanTransaction(addRequest(1, 3_000_000)));
        assertTrue(ex.getMessage().contains("2000000"));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    @Test
    void addPinjamanTransaction_nominalPasSamaDenganSisaPlafond_diizinkan() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(10_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(8_000_000L);
        stubSaveDenganId(5);

        assertDoesNotThrow(() -> service.addPinjamanTransaction(addRequest(null, 2_000_000)));
    }

    @Test
    void addPinjamanTransaction_totalPlafondNull_dianggapNol() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(null)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        assertThrows(IllegalArgumentException.class, () -> service.addPinjamanTransaction(addRequest(null, 1)));
    }

    @Test
    void addPinjamanTransaction_pinjamanTidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.empty());
        when(pinjamanRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addPinjamanTransaction(addRequest(1, 1)));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    // ---------- updatePinjamanTransaction: otorisasi status ----------

    private void update(String status) {
        service.updatePinjamanTransaction(3, 7, null, 5_000_000, 12, status,
                LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 10), "m", "bm", "bo", null);
    }

    private void stubUpdateBerhasil() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
    }

    @Test
    void update_superAdmin_bolehSemuaStatus() {
        loginWithRole("SUPERADMIN");
        stubUpdateBerhasil();
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> update("Disetujui"));
        verify(pinjamanTransactionRepository).save(trx);
    }

    @Test
    void update_roleNull_sesiInvalid() {
        loginWithRole(null);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> update("Direview"));
        assertTrue(ex.getMessage().contains("Sesi Invalid"));
        verifyNoInteractions(pinjamanTransactionRepository);
    }

    @Test
    void update_direview_hanyaMarketing() {
        loginWithRole("BACKOFFICE");

        assertThrows(AccessDeniedException.class, () -> update("Direview"));
        verifyNoInteractions(pinjamanTransactionRepository);
    }

    @Test
    void update_direview_marketingDiizinkan() {
        loginWithRole("MARKETING");
        stubUpdateBerhasil();

        assertDoesNotThrow(() -> update("Direview"));
        assertEquals("Direview", trx.getStatusPengajuan());
    }

    @Test
    void update_disetujui_hanyaBranchManager() {
        loginWithRole("MARKETING");

        assertThrows(AccessDeniedException.class, () -> update("Disetujui"));
    }

    @Test
    void update_ditolak_hanyaBranchManager() {
        loginWithRole("MARKETING");

        assertThrows(AccessDeniedException.class, () -> update("Ditolak"));
    }

    @Test
    void update_ditolak_branchManagerDiizinkan() {
        loginWithRole("BRANCH_MANAGER");
        stubUpdateBerhasil();

        assertDoesNotThrow(() -> update("Ditolak"));
        assertEquals("Ditolak", trx.getStatusPengajuan());
    }

    @Test
    void update_statusLain_rolaApaPunDiizinkan() {
        loginWithRole("BACKOFFICE");
        stubUpdateBerhasil();

        assertDoesNotThrow(() -> update("Dicairkan"));
    }

    @Test
    void update_principalBukanAppUser_validasiStatusDilewati() {
        loginAs("anonymousUser");
        stubUpdateBerhasil();

        assertDoesNotThrow(() -> update("Disetujui"));
    }

    // ---------- updatePinjamanTransaction: alur data ----------

    @Test
    void update_berhasil_mengisiSemuaField() {
        loginWithRole("SUPERADMIN");
        stubUpdateBerhasil();

        update("Direview");

        assertEquals(customer, trx.getCustomer());
        assertEquals(5_000_000, trx.getNominalPinjaman());
        assertEquals(12, trx.getTenor());
        assertEquals("Direview", trx.getStatusPengajuan());
        assertEquals(LocalDate.of(2026, 1, 5), trx.getTanggalReview());
        assertEquals(LocalDate.of(2026, 1, 10), trx.getTanggalApproval());
        assertEquals("m", trx.getNoteMarketing());
        assertEquals("bm", trx.getNoteBm());
        assertEquals("bo", trx.getNoteBackOffice());
        assertEquals(LocalDate.now(), trx.getLastUpdate());
        verifyNoInteractions(pushNotificationService);
    }

    @Test
    void update_transaksiTidakDitemukan() {
        loginWithRole("SUPERADMIN");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> update("Direview"));
    }

    @Test
    void update_customerTidakDitemukan() {
        loginWithRole("SUPERADMIN");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> update("Direview"));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    @Test
    void update_denganPinjamanIdDanLastUpdateBy() {
        loginWithRole("SUPERADMIN");
        stubUpdateBerhasil();
        PinjamanEntity produkBaru = new PinjamanEntity();
        produkBaru.setPinjamanId(2);
        when(pinjamanRepository.findById(2)).thenReturn(Optional.of(produkBaru));
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        service.updatePinjamanTransaction(3, 7, 2, 5_000_000, 12, "Direview", null, null, null, null, null, 11);

        assertEquals(produkBaru, trx.getPinjaman());
        assertEquals(employee, trx.getLastUpdateBy());
    }

    @Test
    void update_pinjamanTidakDitemukan() {
        loginWithRole("SUPERADMIN");
        stubUpdateBerhasil();
        when(pinjamanRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.updatePinjamanTransaction(3, 7, 2, 1, 12, "Direview", null, null, null, null, null, null));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    @Test
    void update_employeeTidakDitemukan() {
        loginWithRole("SUPERADMIN");
        stubUpdateBerhasil();
        when(employeRepository.findById(11)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.updatePinjamanTransaction(3, 7, null, 1, 12, "Direview", null, null, null, null, null, 11));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    // ---------- updatePinjamanTransaction: validasi plafond saat Disetujui ----------

    @Test
    void update_disetujui_nominalMelebihiSisaPlafond() {
        loginWithRole("BRANCH_MANAGER");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(6_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(2_000_000L);

        assertThrows(IllegalArgumentException.class, () -> update("Disetujui"));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    @Test
    void update_disetujui_nominalMasihDalamPlafond() {
        loginWithRole("BRANCH_MANAGER");
        stubUpdateBerhasil();
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(10_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(1_000_000L);

        assertDoesNotThrow(() -> update("Disetujui"));
        assertEquals("Disetujui", trx.getStatusPengajuan());
    }

    // ---------- updatePinjamanTransaction: notifikasi pencairan ----------

    @Test
    void update_menjadiDicairkan_mengirimPushNotification() {
        loginWithRole("BACKOFFICE");
        stubUpdateBerhasil();

        update("Dicairkan");

        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> channel = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> deepLink = ArgumentCaptor.forClass(String.class);
        verify(pushNotificationService).sendToCustomer(eq(7), title.capture(), body.capture(), channel.capture(),
                deepLink.capture());
        assertEquals("Pinjaman Berhasil Dicairkan", title.getValue());
        assertTrue(body.getValue().contains("TRX-2026-00003"));
        assertTrue(body.getValue().contains("Rp"));
        assertEquals("transaction", channel.getValue());
        assertEquals("okariru://status-pinjaman/3", deepLink.getValue());
    }

    @Test
    void update_sudahDicairkanSebelumnya_tidakKirimNotifikasiLagi() {
        loginWithRole("BACKOFFICE");
        trx.setStatusPengajuan("Dicairkan");
        stubUpdateBerhasil();

        update("Dicairkan");

        verify(pushNotificationService, never()).sendToCustomer(anyInt(), anyString(), anyString(), anyString(), any());
    }

    // ---------- reviewPinjamanTransaction ----------

    @Test
    void review_marketingDiizinkan_statusPengajuan() {
        loginWithRole("MARKETING");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        service.reviewPinjamanTransaction(3, "catatan review");

        assertEquals("Direview", trx.getStatusPengajuan());
        assertEquals("catatan review", trx.getNoteMarketing());
        assertEquals(LocalDate.now(), trx.getTanggalReview());
        verify(pinjamanTransactionRepository).save(trx);
    }

    @Test
    void review_roleSelainMarketing_ditolak() {
        loginWithRole("BACKOFFICE");

        assertThrows(AccessDeniedException.class, () -> service.reviewPinjamanTransaction(3, "x"));
        verifyNoInteractions(pinjamanTransactionRepository);
    }

    @Test
    void review_statusBukanPengajuan_ditolak() {
        loginWithRole("MARKETING");
        trx.setStatusPengajuan("Direview");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.reviewPinjamanTransaction(3, "x"));
        assertTrue(ex.getMessage().contains("Pengajuan"));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    @Test
    void review_superAdmin_bypassStatusCheck() {
        loginWithRole("SUPERADMIN");
        trx.setStatusPengajuan("Disetujui");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        assertDoesNotThrow(() -> service.reviewPinjamanTransaction(3, "x"));
        assertEquals("Direview", trx.getStatusPengajuan());
    }

    // ---------- approvalPinjamanTransaction ----------

    @Test
    void approval_branchManagerDiizinkan_disetujui() {
        loginWithRole("BRANCH_MANAGER");
        trx.setStatusPengajuan("Direview");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(10_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(1_000_000L);

        service.approvalPinjamanTransaction(3, true, "oke");

        assertEquals("Disetujui", trx.getStatusPengajuan());
        assertEquals("oke", trx.getNoteBm());
        verify(pinjamanTransactionRepository).save(trx);
    }

    @Test
    void approval_branchManagerDiizinkan_ditolak_tanpaCekPlafond() {
        loginWithRole("BRANCH_MANAGER");
        trx.setStatusPengajuan("Direview");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        service.approvalPinjamanTransaction(3, false, "gagal syarat");

        assertEquals("Ditolak", trx.getStatusPengajuan());
        verifyNoInteractions(plafondRepository);
    }

    @Test
    void approval_ditolak_mengirimNotifikasiDenganAlasan() {
        loginWithRole("BRANCH_MANAGER");
        trx.setStatusPengajuan("Direview");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        service.approvalPinjamanTransaction(3, false, "gagal syarat");

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(pushNotificationService).sendToCustomer(eq(7), eq("Pengajuan Pinjaman Ditolak"), body.capture(),
                eq("transaction"), eq("okariru://status-pinjaman/3"));
        assertTrue(body.getValue().contains("TRX-2026-00003"));
        assertTrue(body.getValue().contains("gagal syarat"));
    }

    @Test
    void approval_disetujui_tidakMengirimNotifikasi() {
        loginWithRole("BRANCH_MANAGER");
        trx.setStatusPengajuan("Direview");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(10_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(1_000_000L);

        service.approvalPinjamanTransaction(3, true, "oke");

        verifyNoInteractions(pushNotificationService);
    }

    @Test
    void approval_roleSelainBranchManager_ditolak() {
        loginWithRole("MARKETING");

        assertThrows(AccessDeniedException.class, () -> service.approvalPinjamanTransaction(3, true, "x"));
        verifyNoInteractions(pinjamanTransactionRepository);
    }

    @Test
    void approval_statusBukanDireview_ditolak() {
        loginWithRole("BRANCH_MANAGER");
        trx.setStatusPengajuan("Pengajuan");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        assertThrows(IllegalArgumentException.class, () -> service.approvalPinjamanTransaction(3, true, "x"));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    @Test
    void approval_nominalMelebihiSisaPlafond_ditolak() {
        loginWithRole("BRANCH_MANAGER");
        trx.setStatusPengajuan("Direview");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond(1_000_000)));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        assertThrows(IllegalArgumentException.class, () -> service.approvalPinjamanTransaction(3, true, "x"));
        verify(pinjamanTransactionRepository, never()).save(any());
    }

    // ---------- disbursePinjamanTransaction ----------

    @Test
    void disburse_backofficeDiizinkan_mengirimNotifikasi() {
        loginWithRole("BACKOFFICE");
        trx.setStatusPengajuan("Disetujui");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        service.disbursePinjamanTransaction(3, "cair");

        assertEquals("Dicairkan", trx.getStatusPengajuan());
        assertEquals("cair", trx.getNoteBackOffice());
        verify(pinjamanTransactionRepository).save(trx);
        verify(pushNotificationService).sendToCustomer(eq(7), eq("Pinjaman Berhasil Dicairkan"), anyString(), eq("transaction"), anyString());
    }

    @Test
    void disburse_roleSelainBackoffice_ditolak() {
        loginWithRole("BRANCH_MANAGER");

        assertThrows(AccessDeniedException.class, () -> service.disbursePinjamanTransaction(3, "x"));
        verifyNoInteractions(pinjamanTransactionRepository);
        verifyNoInteractions(pushNotificationService);
    }

    @Test
    void disburse_statusBukanDisetujui_ditolak() {
        loginWithRole("BACKOFFICE");
        trx.setStatusPengajuan("Direview");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        assertThrows(IllegalArgumentException.class, () -> service.disbursePinjamanTransaction(3, "x"));
        verify(pinjamanTransactionRepository, never()).save(any());
        verifyNoInteractions(pushNotificationService);
    }

    @Test
    void disburse_transaksiTidakDitemukan() {
        loginWithRole("BACKOFFICE");
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.disbursePinjamanTransaction(3, "x"));
    }

    // ---------- deletePinjamanTransaction ----------

    @Test
    void deletePinjamanTransaction_berhasil() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.of(trx));

        String msg = service.deletePinjamanTransaction(3);

        assertTrue(msg.contains("3"));
        verify(pinjamanTransactionRepository).delete(trx);
    }

    @Test
    void deletePinjamanTransaction_tidakDitemukan() {
        when(pinjamanTransactionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deletePinjamanTransaction(3));
        verify(pinjamanTransactionRepository, never()).delete(any());
    }
}
