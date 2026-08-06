package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.service.employeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class employeController {
    private final employeService employeService;

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
}
