package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.service.MenuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private MenuController controller;

    private MenuResponse.getMenuResponse menu() {
        return new MenuResponse.getMenuResponse(1, "Dashboard", "desk", "/dashboard", "home", null, null);
    }

    @Test
    void findAll() {
        Page<MenuResponse.getMenuResponse> page = new PageImpl<>(List.of(menu()));
        when(menuService.findAll("dash", 0, 5)).thenReturn(page);

        ResponseEntity<Page<MenuResponse.getMenuResponse>> result = controller.findAll("dash", 0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void addMenu() {
        MenuRequest.menuAddRequest req = new MenuRequest.menuAddRequest();
        when(menuService.addMenu(req)).thenReturn(menu());

        assertEquals("Dashboard", controller.addMenu(req).getBody().getNamaMenu());
    }

    @Test
    void updateMenu_memanggilServiceDanMengembalikanPesan() {
        MenuRequest.menuUpdateRequest req = new MenuRequest.menuUpdateRequest();
        req.namaMenu = "Baru";
        req.deskripsiMenu = "Desk";
        req.path = "/baru";
        req.icon = "star";

        ResponseEntity<MenuResponse.menuUpdateResponse> result = controller.updateMenu(1, req);

        verify(menuService).updatemenu(1, "Baru", "Desk", "/baru", "star");
        assertEquals("Role Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deleteRole_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<MenuResponse.menuDeleteResponse> result = controller.deleteRole(1);

        verify(menuService).deleteMenu(1);
        assertEquals("Delete role successfully", result.getBody().getMessage());
        assertEquals("Successfuly Deleted", result.getBody().getStatus());
    }

    @Test
    void getMyMenu_memakaiUsernameDariAuthentication() {
        List<MenuResponse.myMenuResponse> list = List.of(new MenuResponse.myMenuResponse(1, "Dashboard", "/d", "home"));
        when(authentication.getName()).thenReturn("budi");
        when(menuService.getMyMenu("budi")).thenReturn(list);

        assertEquals(list, controller.getMyMenu(authentication).getBody());
    }
}
