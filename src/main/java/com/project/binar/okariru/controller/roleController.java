package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.employeRequest;
import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.service.roleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class roleController {
    private final roleService roleService;

    //get all Role
    @GetMapping
    public ResponseEntity<List<roleResponse.getRoleResponse>> findAll() {
        try {
            List<roleResponse.getRoleResponse> list = roleService.getAllRoleService();
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //add role
    @PostMapping
    public ResponseEntity <roleResponse.getRoleResponse> addRole(
            @RequestBody roleRequest.roleAddRequest roleadd){
        try {
            roleResponse.getRoleResponse roleadds = roleService.addRole(roleadd);
            return ResponseEntity.ok(roleadds);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
