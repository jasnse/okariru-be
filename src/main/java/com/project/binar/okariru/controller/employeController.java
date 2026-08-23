package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.EmployeRequest;
import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.service.EmployeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeController {
    private final EmployeService employeService;

    //get all employee with pagination
    @GetMapping
    public ResponseEntity<Page<EmployeResponse.employeGetResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(employeService.findAll(keyword, page, size));
    }

    //findbyusername by headers di postmannya
    @GetMapping(headers = "namaYangDicari")
    public ResponseEntity<EmployeResponse.employeGetResponse> findByName(@RequestHeader("namaYangDicari") String name) {
        return ResponseEntity.ok(employeService.getEmployeeServiceUserName(name));
    }

    //add employee
    @PostMapping
    public ResponseEntity<EmployeResponse.employeAddResponse> addEmployee(
            @Valid @RequestBody EmployeRequest.employeAddRequest employe) {
        return ResponseEntity.ok(employeService.addEmploye(employe));
    }

    @PutMapping
    public ResponseEntity<EmployeResponse.employeUpdateResponse> updateEmployee(
            @RequestParam Integer id,
            @Valid
            @RequestBody EmployeRequest.employeChangeCredentialRequest request
    ) {
        employeService.updateEmployeEmailPass(id, request.email, request.password);

        EmployeResponse.employeUpdateResponse respUpdate = new EmployeResponse.employeUpdateResponse();
        respUpdate.setMessage("Email dan Password Berhasil di Update!");
        return ResponseEntity.ok(respUpdate);
    }

    @DeleteMapping
    public ResponseEntity<EmployeResponse.employeDeleteResponse> deleteEmployee(@RequestParam Integer id) {
        employeService.deleteEmployee(id);

        EmployeResponse.employeDeleteResponse respDelete = new EmployeResponse.employeDeleteResponse();
        respDelete.setMessage("Delete employee successfully");
        respDelete.setStatus("Successfuly Deleted");
        return ResponseEntity.ok(respDelete);
    }
}
