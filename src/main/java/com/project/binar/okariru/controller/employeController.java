package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.employeRequest;
import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.service.employeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class employeController {
    private final employeService employeService;

    //get all employee with pagination
    @GetMapping
    public ResponseEntity<Page<employeResponse.employeGetResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(employeService.findAll(keyword, page, size));
    }

    //findbyusername by headers di postmannya
    @GetMapping(headers = "namaYangDicari")
    public ResponseEntity<employeResponse.employeGetResponse> findByName(@RequestHeader("namaYangDicari") String name) {
        return ResponseEntity.ok(employeService.getEmployeeServiceUserName(name));
    }

    //add employee
    @PostMapping
    public ResponseEntity<employeResponse.employeAddResponse> addEmployee(
            @Valid @RequestBody employeRequest.employeAddRequest employe) {
        return ResponseEntity.ok(employeService.addEmploye(employe));
    }

    @PutMapping
    public ResponseEntity<employeResponse.employeUpdateResponse> updateEmployee(
            @RequestParam Integer id,
            @Valid
            @RequestBody employeRequest.employeChangeCredentialRequest request
    ) {
        employeService.updateEmployeEmailPass(id, request.email, request.password);

        employeResponse.employeUpdateResponse respUpdate = new employeResponse.employeUpdateResponse();
        respUpdate.setMessage("Email dan Password Berhasil di Update!");
        return ResponseEntity.ok(respUpdate);
    }

    @DeleteMapping
    public ResponseEntity<employeResponse.employeDeleteResponse> deleteEmployee(@RequestParam Integer Id) {
        employeService.deleteEmployee(Id);

        employeResponse.employeDeleteResponse respDelete = new employeResponse.employeDeleteResponse();
        respDelete.setMessage("Delete employee successfully");
        respDelete.setStatus("Successfuly Deleted");
        return ResponseEntity.ok(respDelete);
    }
}
