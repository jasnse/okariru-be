package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.dto.RolegroupRequest;
import com.project.binar.okariru.dto.RolegroupResponse;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.RoleEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.RoleRepository;
import com.project.binar.okariru.repository.RolegroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolegroupServiceImplTest {

    @Mock
    private RolegroupRepository rolegroupRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private RolegroupServiceImpl service;

    private RoleEntity role;
    private RolegroupEntity group;
    private EmployeEntity employee;

    @BeforeEach
    void setUp() {
        role = new RoleEntity();
        role.setRoleId(2);

        group = new RolegroupEntity();
        group.setRoleGroupId(5);
        group.setRole(role);
        group.setNamaGroupRole("Tim Kredit");
        group.setCreatedAt(LocalDate.of(2026, 1, 1));

        employee = new EmployeEntity();
        employee.setEmployeeId(11);
        employee.setUserName("budi");
        employee.setEmail("budi@mail.com");
        employee.setNip("123");
    }

    // ---------- getAllRoleGroup / getRoleById ----------

    @Test
    void getAllRoleGroup_memetakanHasilSearch() {
        when(rolegroupRepository.searchRoleGroup("kre")).thenReturn(List.of(group));

        List<RolegroupResponse.getRoleGroupResponse> result = service.getAllRoleGroup("kre");

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRoleGroupId());
        assertEquals(2, result.get(0).getRoleId());
        assertEquals("Tim Kredit", result.get(0).getNamaGroupRole());
    }

    @Test
    void getRoleById_ditemukan() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));

        assertEquals(5, service.getRoleById(5).getRoleGroupId());
    }

    @Test
    void getRoleById_tidakDitemukan() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getRoleById(5));
    }

    // ---------- addRoleGroup ----------

    @Test
    void addRoleGroup_berhasil() {
        RolegroupRequest.roleGroupAddRequest req = new RolegroupRequest.roleGroupAddRequest();
        req.roleId = 2;
        req.namaGroupRole = "Tim Baru";
        when(roleRepository.findById(2)).thenReturn(Optional.of(role));
        when(rolegroupRepository.save(any(RolegroupEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        RolegroupResponse.getRoleGroupResponse r = service.addRoleGroup(req);

        assertEquals("Tim Baru", r.getNamaGroupRole());
        assertEquals(2, r.getRoleId());
        assertEquals(LocalDate.now(), r.getCreatedAt());
    }

    @Test
    void addRoleGroup_roleTidakDitemukan() {
        RolegroupRequest.roleGroupAddRequest req = new RolegroupRequest.roleGroupAddRequest();
        req.roleId = 2;
        when(roleRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addRoleGroup(req));
        verify(rolegroupRepository, never()).save(any());
    }

    // ---------- updateRoleGroup ----------

    @Test
    void updateRoleGroup_berhasil() {
        RoleEntity roleBaru = new RoleEntity();
        roleBaru.setRoleId(3);
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(roleRepository.findById(3)).thenReturn(Optional.of(roleBaru));

        service.updateRoleGroup(5, 3, "Nama Baru");

        verify(rolegroupRepository).save(group);
        assertEquals("Nama Baru", group.getNamaGroupRole());
        assertEquals(roleBaru, group.getRole());
        assertEquals(LocalDate.now(), group.getUpdatedAt());
    }

    @Test
    void updateRoleGroup_groupTidakDitemukan() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateRoleGroup(5, 3, "x"));
    }

    @Test
    void updateRoleGroup_roleTidakDitemukan() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(roleRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateRoleGroup(5, 3, "x"));
        verify(rolegroupRepository, never()).save(any());
    }

    // ---------- deleteRoleGroup ----------

    @Test
    void deleteRoleGroup_tanpaMember_employeesNull() {
        group.setEmployees(null);
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));

        String msg = service.deleteRoleGroup(5);

        assertTrue(msg.contains("5"));
        verify(rolegroupRepository).delete(group);
    }

    @Test
    void deleteRoleGroup_tanpaMember_employeesKosong() {
        group.setEmployees(new ArrayList<>());
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));

        service.deleteRoleGroup(5);

        verify(rolegroupRepository).delete(group);
    }

    @Test
    void deleteRoleGroup_masihPunyaMember() {
        group.setEmployees(List.of(employee));
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.deleteRoleGroup(5));
        assertTrue(ex.getMessage().contains("1 member"));
        verify(rolegroupRepository, never()).delete(any());
    }

    @Test
    void deleteRoleGroup_tidakDitemukan() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteRoleGroup(5));
    }

    // ---------- getMembers ----------

    @Test
    void getMembers_berhasil() {
        when(rolegroupRepository.existsById(5)).thenReturn(true);
        when(employeRepository.searchMembersByRoleGroup(eq(5), eq("bud"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee)));

        Page<RolegroupResponse.roleGroupMemberResponse> result = service.getMembers(5, "bud", 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("budi", result.getContent().get(0).getUserName());
        assertEquals("123", result.getContent().get(0).getNip());
    }

    @Test
    void getMembers_groupTidakDitemukan() {
        when(rolegroupRepository.existsById(5)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.getMembers(5, null, 0, 10));
        verifyNoInteractions(employeRepository);
    }

    // ---------- findEmployeesNonRG ----------

    @Test
    void findEmployeesNonRG_memetakanEmployee() {
        when(employeRepository.findEmployeesWithoutRoleGroup()).thenReturn(List.of(employee));

        List<EmployeResponse.employeGetResponse> result = service.findEmployeesNonRG();

        assertEquals(1, result.size());
        assertEquals(11, result.get(0).Id());
        assertEquals("budi@mail.com", result.get(0).email());
    }

    // ---------- assignEmployee ----------

    @Test
    void assignEmployee_berhasil() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        service.assignEmployee(5, 11);

        assertEquals(group, employee.getRoleGroup());
        verify(employeRepository).save(employee);
    }

    @Test
    void assignEmployee_groupTidakDitemukan() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.assignEmployee(5, 11));
    }

    @Test
    void assignEmployee_employeeTidakDitemukan() {
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(employeRepository.findById(11)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.assignEmployee(5, 11));
    }

    @Test
    void assignEmployee_sudahDiGroupLain() {
        employee.setRoleGroup(new RolegroupEntity());
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        assertThrows(IllegalArgumentException.class, () -> service.assignEmployee(5, 11));
        verify(employeRepository, never()).save(any());
    }

    // ---------- removeEmployee ----------

    @Test
    void removeEmployee_berhasil() {
        employee.setRoleGroup(group);
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        service.removeEmployee(5, 11);

        assertNull(employee.getRoleGroup());
        verify(employeRepository).save(employee);
    }

    @Test
    void removeEmployee_employeeTidakDitemukan() {
        when(employeRepository.findById(11)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.removeEmployee(5, 11));
    }

    @Test
    void removeEmployee_employeeTidakPunyaGroup() {
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        assertThrows(EntityNotFoundException.class, () -> service.removeEmployee(5, 11));
        verify(employeRepository, never()).save(any());
    }

    @Test
    void removeEmployee_groupBerbeda() {
        employee.setRoleGroup(group);
        when(employeRepository.findById(11)).thenReturn(Optional.of(employee));

        assertThrows(EntityNotFoundException.class, () -> service.removeEmployee(99, 11));
        verify(employeRepository, never()).save(any());
    }
}
