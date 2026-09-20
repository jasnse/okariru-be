package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.MenugroupEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.MenuRepository;
import com.project.binar.okariru.repository.MenugroupRepository;
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
class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenugroupRepository menugroupRepository;
    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private MenuServiceImpl service;

    private MenuEntity menu;

    @BeforeEach
    void setUp() {
        menu = new MenuEntity();
        menu.setMenuId(1);
        menu.setNamaMenu("Dashboard");
        menu.setDeskripsiMenu("Halaman utama");
        menu.setPath("/dashboard");
        menu.setIcon("home");
        menu.setCreatedAt(LocalDate.of(2026, 1, 1));
    }

    private MenuRequest.menuAddRequest addRequest() {
        MenuRequest.menuAddRequest req = new MenuRequest.menuAddRequest();
        req.namaMenu = "Dashboard";
        req.deskripsiMenu = "Halaman utama";
        req.path = "/dashboard";
        req.icon = "home";
        return req;
    }

    // ---------- findAll ----------

    @Test
    void findAll_memetakanPageDanMengurutkanBerdasarkanMenuId() {
        when(menuRepository.searchMenu(eq("dash"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(menu)));

        Page<MenuResponse.getMenuResponse> result = service.findAll("dash", 1, 3);

        assertEquals("Dashboard", result.getContent().get(0).getNamaMenu());
        assertEquals("/dashboard", result.getContent().get(0).getPath());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(menuRepository).searchMenu(eq("dash"), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(3, captor.getValue().getPageSize());
        assertNotNull(captor.getValue().getSort().getOrderFor("menuId"));
    }

    // ---------- getMyMenu ----------

    @Test
    void getMyMenu_employeeTidakDitemukan() {
        when(employeRepository.findByUsernameWithRoles("budi")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getMyMenu("budi"));
    }

    @Test
    void getMyMenu_tanpaRoleGroup_mengembalikanListKosong() {
        EmployeEntity employee = new EmployeEntity();
        when(employeRepository.findByUsernameWithRoles("budi")).thenReturn(Optional.of(employee));

        assertTrue(service.getMyMenu("budi").isEmpty());
        verifyNoInteractions(menugroupRepository);
    }

    @Test
    void getMyMenu_denganRoleGroup_mengembalikanMenuMilikGroup() {
        RolegroupEntity group = new RolegroupEntity();
        group.setRoleGroupId(5);
        EmployeEntity employee = new EmployeEntity();
        employee.setRoleGroup(group);
        MenugroupEntity mg = new MenugroupEntity();
        mg.setMenu(menu);
        when(employeRepository.findByUsernameWithRoles("budi")).thenReturn(Optional.of(employee));
        when(menugroupRepository.findByRole_RoleGroupId(5)).thenReturn(List.of(mg));

        List<MenuResponse.myMenuResponse> result = service.getMyMenu("budi");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getMenuId());
        assertEquals("home", result.get(0).getIcon());
    }

    // ---------- addMenu ----------

    @Test
    void addMenu_berhasil() {
        when(menuRepository.existsByNamaMenu("Dashboard")).thenReturn(false);
        when(menuRepository.save(any(MenuEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuResponse.getMenuResponse r = service.addMenu(addRequest());

        assertEquals("Dashboard", r.getNamaMenu());
        assertEquals(LocalDate.now(), r.getCreated_at());
    }

    @Test
    void addMenu_namaSudahAda() {
        when(menuRepository.existsByNamaMenu("Dashboard")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.addMenu(addRequest()));
        verify(menuRepository, never()).save(any());
    }

    // ---------- updatemenu ----------

    @Test
    void updatemenu_berhasil() {
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(menuRepository.existsByNamaMenuAndMenuIdNot("Baru", 1)).thenReturn(false);

        service.updatemenu(1, "Baru", "Desk baru", "/baru", "star");

        assertEquals("Baru", menu.getNamaMenu());
        assertEquals("Desk baru", menu.getDeskripsiMenu());
        assertEquals("/baru", menu.getPath());
        assertEquals("star", menu.getIcon());
        assertEquals(LocalDate.now(), menu.getUpdatedAt());
        verify(menuRepository).save(menu);
    }

    @Test
    void updatemenu_menuTidakDitemukan() {
        when(menuRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updatemenu(1, "a", "b", "c", "d"));
    }

    @Test
    void updatemenu_namaSudahDipakaiMenuLain() {
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(menuRepository.existsByNamaMenuAndMenuIdNot("Baru", 1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.updatemenu(1, "Baru", "b", "c", "d"));
        verify(menuRepository, never()).save(any());
    }

    // ---------- deleteMenu ----------

    @Test
    void deleteMenu_berhasil() {
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));

        String msg = service.deleteMenu(1);

        assertTrue(msg.contains("1"));
        verify(menuRepository).delete(menu);
    }

    @Test
    void deleteMenu_tidakDitemukan() {
        when(menuRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteMenu(1));
        verify(menuRepository, never()).delete(any());
    }
}
