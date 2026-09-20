package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.EmployeRequest;
import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.service.EmployeService;
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
class EmployeControllerTest {

    @Mock
    private EmployeService employeService;

    @InjectMocks
    private EmployeController controller;

    private EmployeResponse.employeGetResponse emp() {
        return new EmployeResponse.employeGetResponse(11, "budi", "123", "b@mail.com", null, null);
    }

    @Test
    void findAll() {
        Page<EmployeResponse.employeGetResponse> page = new PageImpl<>(List.of(emp()));
        when(employeService.findAll("bud", 0, 5)).thenReturn(page);

        ResponseEntity<Page<EmployeResponse.employeGetResponse>> result = controller.findAll("bud", 0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
    }

    @Test
    void findByName() {
        when(employeService.getEmployeeServiceUserName("budi")).thenReturn(emp());

        assertEquals("budi", controller.findByName("budi").getBody().userName());
    }

    @Test
    void addEmployee() {
        EmployeRequest.employeAddRequest req = new EmployeRequest.employeAddRequest();
        EmployeResponse.employeAddResponse resp = new EmployeResponse.employeAddResponse(11, "budi", "123", null);
        when(employeService.addEmploye(req)).thenReturn(resp);

        assertEquals(11, controller.addEmployee(req).getBody().getId());
    }

    @Test
    void updateEmployee_memanggilServiceDanMengembalikanPesan() {
        EmployeRequest.employeChangeCredentialRequest req = new EmployeRequest.employeChangeCredentialRequest();
        req.email = "baru@mail.com";
        req.password = "pass";

        ResponseEntity<EmployeResponse.employeUpdateResponse> result = controller.updateEmployee(11, req);

        verify(employeService).updateEmployeEmailPass(11, "baru@mail.com", "pass");
        assertEquals("Email dan Password Berhasil di Update!", result.getBody().getMessage());
    }

    @Test
    void deleteEmployee_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<EmployeResponse.employeDeleteResponse> result = controller.deleteEmployee(11);

        verify(employeService).deleteEmployee(11);
        assertEquals("Delete employee successfully", result.getBody().getMessage());
        assertEquals("Successfuly Deleted", result.getBody().getStatus());
    }
}
