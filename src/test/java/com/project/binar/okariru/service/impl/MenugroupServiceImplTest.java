package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.MenugroupEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.MenuRepository;
import com.project.binar.okariru.repository.MenugroupRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenugroupServiceImplTest {

    @Mock
    private MenugroupRepository menugroupRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private RolegroupRepository rolegroupRepository;

    @InjectMocks
    private MenugroupServiceImpl service;

    private MenuEntity menu;
    private RolegroupEntity group;
    private MenugroupEntity menuGroup;

    @BeforeEach
    void setUp() {
        menu = new MenuEntity();
        menu.setMenuId(1);
        menu.setNamaMenu("Dashboard");

        group = new RolegroupEntity();
        group.setRoleGroupId(5);

        menuGroup = new MenugroupEntity();
        menuGroup.setMenuGroupId(9);
        menuGroup.setMenu(menu);
        menuGroup.setRole(group);
        menuGroup.setCreatedAt(LocalDate.of(2026, 1, 1));
    }

    private MenugroupRequest.menuGroupAddRequest addRequest() {
        MenugroupRequest.menuGroupAddRequest req = new MenugroupRequest.menuGroupAddRequest();
        req.menuId = 1;
        req.roleGroupId = 5;
        return req;
    }

    // ---------- getAllMenuGroup ----------

    @Test
    void getAllMenuGroup_memetakanSemua() {
        when(menugroupRepository.findAll()).thenReturn(List.of(menuGroup));

        List<MenugroupResponse.getMenuGroupResponse> result = service.getAllMenuGroup();

        assertEquals(1, result.size());
        assertEquals(9, result.get(0).getMenuGroupId());
        assertEquals("Dashboard", result.get(0).getNamaMenu());
        assertEquals(5, result.get(0).getRoleGroupId());
    }

    // ---------- getMenuGroupsByRoleGroup ----------

    @Test
    void getMenuGroupsByRoleGroup_berhasil() {
        when(rolegroupRepository.existsById(5)).thenReturn(true);
        when(menugroupRepository.searchByRoleGroup(eq(5), eq("dash"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(menuGroup)));

        Page<MenugroupResponse.getMenuGroupResponse> result = service.getMenuGroupsByRoleGroup(5, "dash", 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getMenuId());
    }

    @Test
    void getMenuGroupsByRoleGroup_roleGroupTidakDitemukan() {
        when(rolegroupRepository.existsById(5)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.getMenuGroupsByRoleGroup(5, null, 0, 10));
        verifyNoInteractions(menugroupRepository);
    }

    // ---------- getMenusNotInRoleGroup ----------

    @Test
    void getMenusNotInRoleGroup_berhasil() {
        when(rolegroupRepository.existsById(5)).thenReturn(true);
        when(menuRepository.findMenusNotInRoleGroup(5)).thenReturn(List.of(menu));

        List<MenuResponse.getMenuResponse> result = service.getMenusNotInRoleGroup(5);

        assertEquals(1, result.size());
        assertEquals("Dashboard", result.get(0).getNamaMenu());
    }

    @Test
    void getMenusNotInRoleGroup_roleGroupTidakDitemukan() {
        when(rolegroupRepository.existsById(5)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.getMenusNotInRoleGroup(5));
        verifyNoInteractions(menuRepository);
    }

    // ---------- getMenuGroupById ----------

    @Test
    void getMenuGroupById_ditemukan() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.of(menuGroup));

        assertEquals(9, service.getMenuGroupById(9).getMenuGroupId());
    }

    @Test
    void getMenuGroupById_tidakDitemukan() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getMenuGroupById(9));
    }

    // ---------- addMenuGroup ----------

    @Test
    void addMenuGroup_berhasil() {
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(menugroupRepository.existsByMenu_MenuIdAndRole_RoleGroupId(1, 5)).thenReturn(false);
        when(menugroupRepository.save(any(MenugroupEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        MenugroupResponse.getMenuGroupResponse r = service.addMenuGroup(addRequest());

        assertEquals(1, r.getMenuId());
        assertEquals(5, r.getRoleGroupId());
        assertEquals(LocalDate.now(), r.getCreatedAt());
    }

    @Test
    void addMenuGroup_menuTidakDitemukan() {
        when(menuRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addMenuGroup(addRequest()));
    }

    @Test
    void addMenuGroup_roleGroupTidakDitemukan() {
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(rolegroupRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addMenuGroup(addRequest()));
    }

    @Test
    void addMenuGroup_kombinasiSudahAda() {
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(menugroupRepository.existsByMenu_MenuIdAndRole_RoleGroupId(1, 5)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.addMenuGroup(addRequest()));
        verify(menugroupRepository, never()).save(any());
    }

    // ---------- updateMenuGroup ----------

    @Test
    void updateMenuGroup_berhasil() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.of(menuGroup));
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(menugroupRepository.existsByMenu_MenuIdAndRole_RoleGroupIdAndMenuGroupIdNot(1, 5, 9)).thenReturn(false);

        service.updateMenuGroup(9, 1, 5);

        assertEquals(LocalDate.now(), menuGroup.getUpdatedAt());
        verify(menugroupRepository).save(menuGroup);
    }

    @Test
    void updateMenuGroup_menuGroupTidakDitemukan() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateMenuGroup(9, 1, 5));
    }

    @Test
    void updateMenuGroup_menuTidakDitemukan() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.of(menuGroup));
        when(menuRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateMenuGroup(9, 1, 5));
    }

    @Test
    void updateMenuGroup_roleGroupTidakDitemukan() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.of(menuGroup));
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(rolegroupRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateMenuGroup(9, 1, 5));
    }

    @Test
    void updateMenuGroup_kombinasiSudahAda() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.of(menuGroup));
        when(menuRepository.findById(1)).thenReturn(Optional.of(menu));
        when(rolegroupRepository.findById(5)).thenReturn(Optional.of(group));
        when(menugroupRepository.existsByMenu_MenuIdAndRole_RoleGroupIdAndMenuGroupIdNot(1, 5, 9)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.updateMenuGroup(9, 1, 5));
        verify(menugroupRepository, never()).save(any());
    }

    // ---------- deleteMenuGroup ----------

    @Test
    void deleteMenuGroup_berhasil() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.of(menuGroup));

        String msg = service.deleteMenuGroup(9);

        assertTrue(msg.contains("9"));
        verify(menugroupRepository).delete(menuGroup);
    }

    @Test
    void deleteMenuGroup_tidakDitemukan() {
        when(menugroupRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteMenuGroup(9));
    }
}
