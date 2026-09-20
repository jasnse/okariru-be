package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.RoleResponse;
import com.project.binar.okariru.service.RoleService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController controller;

    @Test
    void findAll_meneruskanParameter() {
        RoleResponse.getRoleResponse item = new RoleResponse.getRoleResponse(1, "ADMIN", null, null);
        Page<RoleResponse.getRoleResponse> page = new PageImpl<>(List.of(item));
        when(roleService.findAll("adm", 0, 5)).thenReturn(page);

        ResponseEntity<Page<RoleResponse.getRoleResponse>> result = controller.findAll("adm", 0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
        verify(roleService).findAll("adm", 0, 5);
    }

    @Test
    void findByroleId_meneruskanId() {
        when(roleService.getRoleById(1)).thenReturn(new RoleResponse.getRoleResponse(1, "ADMIN", null, null));

        ResponseEntity<RoleResponse.getRoleResponse> result = controller.findByroleId(1);

        assertEquals("ADMIN", result.getBody().getNama_role());
    }
}
