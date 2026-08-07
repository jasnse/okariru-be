package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.employeRequest;
import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.service.employeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class employeController {
    private final employeService employeService;

    //get all employee
    @GetMapping
    public ResponseEntity <List<employeResponse.employeGetResponse>>  findAll(){
        try {
            List<employeResponse.employeGetResponse> list = employeService.getAllEmployeeService();
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {}
        return ResponseEntity.badRequest().build();
    }

    //findbyusername by headers di postmannya
    @GetMapping(headers = "namaYangDicari")
    public ResponseEntity <employeResponse.employeGetResponse> findByName(@RequestHeader("namaYangDicari") String name){
        try {
            employeResponse.employeGetResponse employee = employeService.getEmployeeServiceUserName(name);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity <employeResponse.employeGetResponse> addEmployee(
            @RequestBody employeRequest.employeAddRequest employe){
        try {
            employeResponse.employeGetResponse employee = employeService.addEmploye(employe);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity <employeResponse.employeUpdateResponse> updateEmployee(
            @RequestParam Integer id,
            @RequestBody employeRequest.employeChangeCredentialRequest request
    ) {
        employeResponse.employeUpdateResponse respUpdate = null;
        try {
            employeService.updateEmployeEmailPass(id, request.email, request.password);

            respUpdate = new employeResponse.employeUpdateResponse();
            respUpdate.setMessage("Email dan Password Berhasil di Update!");

            return ResponseEntity.ok(respUpdate);
        }catch (EntityNotFoundException e) {
            respUpdate.setMessage("Gagal Update: User tidak di temukan");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respUpdate);
        }
    }

    @DeleteMapping()
    public ResponseEntity <employeResponse.employeDeleteResponse> deleteEmployee(
            @RequestParam Integer Id
    ) {
        employeResponse.employeDeleteResponse respDelete = null;
        try {
            employeService.deleteEmployee(Id);

            respDelete = new employeResponse.employeDeleteResponse();
            respDelete.setMessage("Delete employee successfully");
            respDelete.setStatus("Successfuly Deleted");
            return ResponseEntity.ok(respDelete);
        } catch (RuntimeException e) {
            respDelete.setMessage("Delete employee failed");
            respDelete.setStatus("Failed");
            return ResponseEntity.badRequest().build();
        }
    }


}
