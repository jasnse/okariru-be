package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.PlafondRequest;
import com.project.binar.okariru.dto.PlafondResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.PlafondEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import com.project.binar.okariru.repository.PlafondRepository;
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
class PlafondServiceImplTest {

    @Mock
    private PlafondRepository plafondRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private EmployeRepository employeRepository;
    @Mock
    private PinjamanTransactionRepository pinjamanTransactionRepository;

    @InjectMocks
    private PlafondServiceImpl service;

    private CustomerEntity customer;
    private EmployeEntity employee;
    private PlafondEntity plafond;

    @BeforeEach
    void setUp() {
        customer = new CustomerEntity();
        customer.setCustomerId(7);

        employee = new EmployeEntity();
        employee.setEmployeeId(3);

        plafond = new PlafondEntity();
        plafond.setPlafondId(1);
        plafond.setUser(customer);
        plafond.setTotalPlafond(10_000);
        plafond.setDeskripsiPlafond("Plafond awal");
        plafond.setCreatedAt(LocalDate.of(2026, 1, 1));
    }

    // ---------- getAllPlafond ----------

    @Test
    void getAllPlafond_hitungSisaPlafondDanMapCreatedByUpdatedBy() {
        plafond.setCreatedBy(employee);
        when(plafondRepository.findAll()).thenReturn(List.of(plafond));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(4_000L);

        List<PlafondResponse.getPlafondResponse> result = service.getAllPlafond();

        assertEquals(1, result.size());
        PlafondResponse.getPlafondResponse r = result.get(0);
        assertEquals(1, r.getPlafondId());
        assertEquals(7, r.getUserId());
        assertEquals(10_000, r.getTotalPlafond());
        assertEquals(6_000L, r.getSisaPlafond());
        assertEquals(3, r.getCreatedBy());
        assertNull(r.getUpdatedBy());
    }

    @Test
    void getAllPlafond_totalPlafondNullDiperlakukanNol() {
        plafond.setTotalPlafond(null);
        when(plafondRepository.findAll()).thenReturn(List.of(plafond));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(500L);

        List<PlafondResponse.getPlafondResponse> result = service.getAllPlafond();

        assertEquals(-500L, result.get(0).getSisaPlafond());
    }

    @Test
    void getAllPlafond_denganUpdatedBy_tanpaCreatedBy() {
        plafond.setUpdatedBy(employee);
        when(plafondRepository.findAll()).thenReturn(List.of(plafond));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        PlafondResponse.getPlafondResponse r = service.getAllPlafond().get(0);

        assertNull(r.getCreatedBy());
        assertEquals(3, r.getUpdatedBy());
    }

    @Test
    void getAllPlafond_kosong() {
        when(plafondRepository.findAll()).thenReturn(List.of());

        assertTrue(service.getAllPlafond().isEmpty());
    }

    // ---------- getPlafondById ----------

    @Test
    void getPlafondById_ditemukan() {
        plafond.setUpdatedBy(employee);
        when(plafondRepository.findById(1)).thenReturn(Optional.of(plafond));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        PlafondResponse.getPlafondResponse r = service.getPlafondById(1);

        assertEquals(10_000L, r.getSisaPlafond());
        assertEquals(3, r.getUpdatedBy());
        assertNull(r.getCreatedBy());
    }

    @Test
    void getPlafondById_denganCreatedBy() {
        plafond.setCreatedBy(employee);
        when(plafondRepository.findById(1)).thenReturn(Optional.of(plafond));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        PlafondResponse.getPlafondResponse r = service.getPlafondById(1);

        assertEquals(3, r.getCreatedBy());
        assertNull(r.getUpdatedBy());
    }

    @Test
    void getPlafondById_tidakDitemukan() {
        when(plafondRepository.findById(99)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.getPlafondById(99));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- getPlafondByCustomerId ----------

    @Test
    void getPlafondByCustomerId_ditemukan() {
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(1_000L);

        PlafondResponse.getPlafondResponse r = service.getPlafondByCustomerId(7);

        assertEquals(9_000L, r.getSisaPlafond());
    }

    @Test
    void getPlafondByCustomerId_denganCreatedByDanUpdatedBy() {
        plafond.setCreatedBy(employee);
        plafond.setUpdatedBy(employee);
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.of(plafond));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        PlafondResponse.getPlafondResponse r = service.getPlafondByCustomerId(7);

        assertEquals(3, r.getCreatedBy());
        assertEquals(3, r.getUpdatedBy());
    }

    @Test
    void getPlafondByCustomerId_tidakDitemukan() {
        when(plafondRepository.findByUser_CustomerId(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getPlafondByCustomerId(7));
    }

    // ---------- addPlafond ----------

    private PlafondRequest.plafondAddRequest addRequest(Integer createdBy) {
        PlafondRequest.plafondAddRequest req = new PlafondRequest.plafondAddRequest();
        req.userId = 7;
        req.totalPlafond = 10_000;
        req.deskripsiPlafond = "Plafond baru";
        req.createdBy = createdBy;
        return req;
    }

    @Test
    void addPlafond_denganCreatedBy() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(employeRepository.findById(3)).thenReturn(Optional.of(employee));
        when(plafondRepository.save(any(PlafondEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        PlafondResponse.getPlafondResponse r = service.addPlafond(addRequest(3));

        assertEquals(7, r.getUserId());
        assertEquals(3, r.getCreatedBy());
        assertEquals(LocalDate.now(), r.getCreatedAt());
        ArgumentCaptor<PlafondEntity> captor = ArgumentCaptor.forClass(PlafondEntity.class);
        verify(plafondRepository).save(captor.capture());
        assertEquals("Plafond baru", captor.getValue().getDeskripsiPlafond());
    }

    @Test
    void addPlafond_hasilSaveMemilikiUpdatedBy_ikutDipetakan() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.save(any(PlafondEntity.class))).thenAnswer(inv -> {
            PlafondEntity e = inv.getArgument(0);
            e.setUpdatedBy(employee);
            return e;
        });
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        PlafondResponse.getPlafondResponse r = service.addPlafond(addRequest(null));

        assertEquals(3, r.getUpdatedBy());
    }

    @Test
    void addPlafond_tanpaCreatedBy() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(plafondRepository.save(any(PlafondEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pinjamanTransactionRepository.sumNominalPinjamanDisetujuiByCustomer(7)).thenReturn(0L);

        PlafondResponse.getPlafondResponse r = service.addPlafond(addRequest(null));

        assertNull(r.getCreatedBy());
        verifyNoInteractions(employeRepository);
    }

    @Test
    void addPlafond_customerTidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addPlafond(addRequest(null)));
        verify(plafondRepository, never()).save(any());
    }

    @Test
    void addPlafond_employeeTidakDitemukan() {
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(employeRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addPlafond(addRequest(3)));
        verify(plafondRepository, never()).save(any());
    }

    // ---------- updatePlafond ----------

    @Test
    void updatePlafond_denganUpdatedBy() {
        when(plafondRepository.findById(1)).thenReturn(Optional.of(plafond));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(employeRepository.findById(3)).thenReturn(Optional.of(employee));

        service.updatePlafond(1, 7, 20_000, "Naik", 3);

        ArgumentCaptor<PlafondEntity> captor = ArgumentCaptor.forClass(PlafondEntity.class);
        verify(plafondRepository).save(captor.capture());
        PlafondEntity saved = captor.getValue();
        assertEquals(20_000, saved.getTotalPlafond());
        assertEquals("Naik", saved.getDeskripsiPlafond());
        assertEquals(employee, saved.getUpdatedBy());
        assertEquals(LocalDate.now(), saved.getUpdatedAt());
    }

    @Test
    void updatePlafond_tanpaUpdatedBy() {
        when(plafondRepository.findById(1)).thenReturn(Optional.of(plafond));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));

        service.updatePlafond(1, 7, 20_000, "Naik", null);

        verify(plafondRepository).save(plafond);
        assertNull(plafond.getUpdatedBy());
        verifyNoInteractions(employeRepository);
    }

    @Test
    void updatePlafond_plafondTidakDitemukan() {
        when(plafondRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updatePlafond(1, 7, 1, "x", null));
        verify(plafondRepository, never()).save(any());
    }

    @Test
    void updatePlafond_customerTidakDitemukan() {
        when(plafondRepository.findById(1)).thenReturn(Optional.of(plafond));
        when(customerRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updatePlafond(1, 7, 1, "x", null));
        verify(plafondRepository, never()).save(any());
    }

    @Test
    void updatePlafond_employeeTidakDitemukan() {
        when(plafondRepository.findById(1)).thenReturn(Optional.of(plafond));
        when(customerRepository.findById(7)).thenReturn(Optional.of(customer));
        when(employeRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updatePlafond(1, 7, 1, "x", 3));
        verify(plafondRepository, never()).save(any());
    }

    // ---------- deletePlafond ----------

    @Test
    void deletePlafond_ditemukan() {
        when(plafondRepository.findById(1)).thenReturn(Optional.of(plafond));

        String msg = service.deletePlafond(1);

        assertTrue(msg.contains("1"));
        verify(plafondRepository).delete(plafond);
    }

    @Test
    void deletePlafond_tidakDitemukan() {
        when(plafondRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deletePlafond(1));
        verify(plafondRepository, never()).delete(any());
    }
}
