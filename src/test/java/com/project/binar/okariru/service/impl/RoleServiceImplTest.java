package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.RoleResponse;
import com.project.binar.okariru.entity.RoleEntity;
import com.project.binar.okariru.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl service;

    private RoleEntity role(int id, String nama) {
        RoleEntity r = new RoleEntity();
        r.setRoleId(id);
        r.setNamaRole(nama);
        r.setCreatedAt(LocalDate.of(2026, 1, 1));
        return r;
    }

    @Test
    void findAll_memetakanPageDanMengurutkanBerdasarkanRoleId() {
        Page<RoleEntity> page = new PageImpl<>(List.of(role(1, "ADMIN"), role(2, "STAFF")));
        when(roleRepository.searchRole(eq("adm"), any(Pageable.class))).thenReturn(page);

        Page<RoleResponse.getRoleResponse> result = service.findAll("adm", 2, 10);

        assertEquals(2, result.getContent().size());
        assertEquals("ADMIN", result.getContent().get(0).getNama_role());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(roleRepository).searchRole(eq("adm"), captor.capture());
        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(10, captor.getValue().getPageSize());
        assertTrue(captor.getValue().getSort().getOrderFor("roleId").isAscending());
    }

    @Test
    void getRoleById_ditemukan() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role(1, "ADMIN")));

        RoleResponse.getRoleResponse r = service.getRoleById(1);

        assertEquals(1, r.getRole_id());
        assertEquals("ADMIN", r.getNama_role());
        assertEquals(LocalDate.of(2026, 1, 1), r.getCreated_at());
    }

    @Test
    void getRoleById_tidakDitemukan() {
        when(roleRepository.findById(9)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.getRoleById(9));
        assertTrue(ex.getMessage().contains("9"));
    }
}
