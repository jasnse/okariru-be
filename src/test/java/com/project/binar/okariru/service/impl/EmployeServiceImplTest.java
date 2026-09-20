package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.EmployeRequest;
import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.EmployeRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceImplTest {

    @Mock
    private EmployeRepository employeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeServiceImpl service;

    private EmployeEntity employee;

    @BeforeEach
    void setUp() {
        employee = new EmployeEntity();
        employee.setEmployeeId(11);
        employee.setUserName("budi");
        employee.setEmail("budi@mail.com");
        employee.setNip("123");
        employee.setJoinedDate(LocalDate.of(2026, 1, 1));
    }

    private EmployeRequest.employeAddRequest addRequest() {
        EmployeRequest.employeAddRequest req = new EmployeRequest.employeAddRequest();
        req.username = "budi";
        req.email = "budi@mail.com";
        req.password = "rahasia";
        req.nip = "123";
        return req;
    }

    @Test
    void findAll_memetakanPage() {
        when(employeRepository.searchEmployees(eq("bud"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee)));

        Page<EmployeResponse.employeGetResponse> result = service.findAll("bud", 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("budi", result.getContent().get(0).userName());
    }

    @Test
    void getAllEmployeeService_memetakanSemuaEmployee() {
        when(employeRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeResponse.employeGetResponse> result = service.getAllEmployeeService();

        assertEquals(1, result.size());
        assertEquals(11, result.get(0).Id());
        assertEquals("123", result.get(0).nip());
    }

    @Test
    void getEmployeeServiceUserName_ditemukan() {
        when(employeRepository.findByUserName("budi")).thenReturn(Optional.of(employee));

        assertEquals("budi@mail.com", service.getEmployeeServiceUserName("budi").email());
    }

    @Test
    void getEmployeeServiceUserName_tidakDitemukan() {
        when(employeRepository.findByUserName("budi")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getEmployeeServiceUserName("budi"));
    }

    @Test
    void addEmploye_berhasilDanPasswordDiEncode() {
        when(employeRepository.existsByUserName("budi")).thenReturn(false);
        when(employeRepository.existsByEmail("budi@mail.com")).thenReturn(false);
        when(employeRepository.existsByNip("123")).thenReturn(false);
        when(passwordEncoder.encode("rahasia")).thenReturn("HASH");
        when(employeRepository.save(any(EmployeEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeResponse.employeAddResponse r = service.addEmploye(addRequest());

        assertEquals("budi", r.getUserName());
        assertEquals(LocalDate.now(), r.getJoinedDate());
        ArgumentCaptor<EmployeEntity> captor = ArgumentCaptor.forClass(EmployeEntity.class);
        verify(employeRepository).save(captor.capture());
        assertEquals("HASH", captor.getValue().getPassword());
    }

    @Test
    void addEmploye_usernameSudahAda() {
        when(employeRepository.existsByUserName("budi")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addEmploye(addRequest()));
        assertTrue(ex.getMessage().contains("Username"));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void addEmploye_emailSudahAda() {
        when(employeRepository.existsByUserName("budi")).thenReturn(false);
        when(employeRepository.existsByEmail("budi@mail.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addEmploye(addRequest()));
        assertTrue(ex.getMessage().contains("Email"));
    }

    @Test
    void addEmploye_nipSudahAda() {
        when(employeRepository.existsByUserName("budi")).thenReturn(false);
        when(employeRepository.existsByEmail("budi@mail.com")).thenReturn(false);
        when(employeRepository.existsByNip("123")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.addEmploye(addRequest()));
        assertTrue(ex.getMessage().contains("NIP"));
    }

    @Test
    void updateEmployeEmailPass_berhasil() {
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));
        when(employeRepository.existsByEmailAndEmployeeIdNot("baru@mail.com", 11)).thenReturn(false);
        when(passwordEncoder.encode("pass-baru")).thenReturn("HASH-BARU");

        service.updateEmployeEmailPass(11, "baru@mail.com", "pass-baru");

        assertEquals("baru@mail.com", employee.getEmail());
        assertEquals("HASH-BARU", employee.getPassword());
        assertEquals(LocalDate.now(), employee.getUpdatedAt());
        verify(employeRepository).save(employee);
    }

    @Test
    void updateEmployeEmailPass_employeeTidakDitemukan() {
        when(employeRepository.findById(11)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateEmployeEmailPass(11, "a@b.c", "x"));
    }

    @Test
    void updateEmployeEmailPass_emailDipakaiEmployeeLain() {
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));
        when(employeRepository.existsByEmailAndEmployeeIdNot("baru@mail.com", 11)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.updateEmployeEmailPass(11, "baru@mail.com", "x"));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void deleteEmployee_berhasil() {
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        String msg = service.deleteEmployee(11);

        assertTrue(msg.contains("11"));
        verify(employeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_tidakDitemukan() {
        when(employeRepository.findById(11)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteEmployee(11));
    }

    @Test
    void deleteEmployee_masihDiRoleGroup() {
        RolegroupEntity group = new RolegroupEntity();
        group.setNamaGroupRole("Tim Kredit");
        employee.setRoleGroup(group);
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.deleteEmployee(11));
        assertTrue(ex.getMessage().contains("Tim Kredit"));
        verify(employeRepository, never()).delete(any());
    }
}
