package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;
import com.project.binar.okariru.service.MenugroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenugroupControllerTest {

    @Mock
    private MenugroupService menugroupService;

    @InjectMocks
    private MenugroupController controller;

    private MenugroupResponse.getMenuGroupResponse mg() {
        return new MenugroupResponse.getMenuGroupResponse(9, 1, "Dashboard", 5, null, null);
    }

    @Test
    void findAll_denganRoleGroupId_memakaiVersiPagination() {
        Page<MenugroupResponse.getMenuGroupResponse> page = new PageImpl<>(List.of(mg()));
        when(menugroupService.getMenuGroupsByRoleGroup(5, "dash", 0, 10)).thenReturn(page);

        ResponseEntity<?> result = controller.findAll(5, "dash", 0, 10);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(page, result.getBody());
        verify(menugroupService, never()).getAllMenuGroup();
    }

    @Test
    void findAll_tanpaRoleGroupId_memakaiGetAll() {
        List<MenugroupResponse.getMenuGroupResponse> list = List.of(mg());
        when(menugroupService.getAllMenuGroup()).thenReturn(list);

        ResponseEntity<?> result = controller.findAll(null, null, 0, 10);

        assertEquals(list, result.getBody());
        verify(menugroupService, never()).getMenuGroupsByRoleGroup(any(), any(), anyInt(), anyInt());
    }

    @Test
    void getMenusNotInRoleGroup() {
        List<MenuResponse.getMenuResponse> list =
                List.of(new MenuResponse.getMenuResponse(1, "Dashboard", "d", "/d", "home", null, null));
        when(menugroupService.getMenusNotInRoleGroup(5)).thenReturn(list);

        assertEquals(list, controller.getMenusNotInRoleGroup(5).getBody());
    }

    @Test
    void getMenuGroupById() {
        when(menugroupService.getMenuGroupById(9)).thenReturn(mg());

        assertEquals(9, controller.getMenuGroupById(9).getBody().getMenuGroupId());
    }

    @Test
    void addMenuGroup() {
        MenugroupRequest.menuGroupAddRequest req = new MenugroupRequest.menuGroupAddRequest();
        when(menugroupService.addMenuGroup(req)).thenReturn(mg());

        assertEquals("Dashboard", controller.addMenuGroup(req).getBody().getNamaMenu());
    }

    @Test
    void updateMenuGroup_memanggilServiceDanMengembalikanPesan() {
        MenugroupRequest.menuGroupUpdateRequest req = new MenugroupRequest.menuGroupUpdateRequest();
        req.menuId = 1;
        req.roleGroupId = 5;

        ResponseEntity<MenugroupResponse.menuGroupUpdateResponse> result = controller.updateMenuGroup(9, req);

        verify(menugroupService).updateMenuGroup(9, 1, 5);
        assertEquals("Menu Group Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deleteMenuGroup_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<MenugroupResponse.menuGroupDeleteResponse> result = controller.deleteMenuGroup(9);

        verify(menugroupService).deleteMenuGroup(9);
        assertEquals("Delete menu group successfully", result.getBody().getMessage());
    }
}
