package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.dto.RolegroupRequest;
import com.project.binar.okariru.dto.RolegroupResponse;
import com.project.binar.okariru.service.RolegroupService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleGroupControllerTest {

    @Mock
    private RolegroupService rolegroupService;

    @InjectMocks
    private RoleGroupController controller;

    private RolegroupResponse.getRoleGroupResponse group() {
        return new RolegroupResponse.getRoleGroupResponse(5, 2, "Tim Kredit", null, null);
    }

    @Test
    void findAll() {
        when(rolegroupService.getAllRoleGroup("kre")).thenReturn(List.of(group()));

        ResponseEntity<List<RolegroupResponse.getRoleGroupResponse>> result = controller.findAll("kre");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getRoleById() {
        when(rolegroupService.getRoleById(5)).thenReturn(group());

        assertEquals(5, controller.getRoleById(5).getBody().getRoleGroupId());
    }

    @Test
    void addRoleGroup() {
        RolegroupRequest.roleGroupAddRequest req = new RolegroupRequest.roleGroupAddRequest();
        when(rolegroupService.addRoleGroup(req)).thenReturn(group());

        assertEquals("Tim Kredit", controller.addRoleGroup(req).getBody().getNamaGroupRole());
    }

    @Test
    void updateRole_memanggilServiceDanMengembalikanPesan() {
        RolegroupRequest.roleGroupUpdateRequest req = new RolegroupRequest.roleGroupUpdateRequest();
        req.roleId = 2;
        req.namaGroupRole = "Baru";

        ResponseEntity<RolegroupResponse.roleGroupUpdateResponse> result = controller.updateRole(5, req);

        verify(rolegroupService).updateRoleGroup(5, 2, "Baru");
        assertEquals("Role Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deleteRoleGroup_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<RolegroupResponse.roleGroupDeleteResponse> result = controller.deleteRoleGroup(5);

        verify(rolegroupService).deleteRoleGroup(5);
        assertEquals("Delete role Group successfully", result.getBody().getMessage());
    }

    @Test
    void getMembers() {
        Page<RolegroupResponse.roleGroupMemberResponse> page = new PageImpl<>(
                List.of(new RolegroupResponse.roleGroupMemberResponse(11, "budi", "b@mail.com", "123")));
        when(rolegroupService.getMembers(5, "bud", 0, 10)).thenReturn(page);

        assertEquals(1, controller.getMembers(5, "bud", 0, 10).getBody().getTotalElements());
    }

    @Test
    void getEmployeeNonRg() {
        List<EmployeResponse.employeGetResponse> list =
                List.of(new EmployeResponse.employeGetResponse(11, "budi", "123", "b@mail.com", null, null));
        when(rolegroupService.findEmployeesNonRG()).thenReturn(list);

        assertEquals(list, controller.getEmployeeNonRg().getBody());
    }

    @Test
    void assignEmployee() {
        RolegroupRequest.assignEmployeeRequest req = new RolegroupRequest.assignEmployeeRequest();
        req.employeeId = 11;

        ResponseEntity<Void> result = controller.assignEmployee(5, req);

        verify(rolegroupService).assignEmployee(5, 11);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void removeEmployee() {
        ResponseEntity<Void> result = controller.removeEmployee(5, 11);

        verify(rolegroupService).removeEmployee(5, 11);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
