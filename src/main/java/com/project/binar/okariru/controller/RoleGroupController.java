package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.*;
import com.project.binar.okariru.service.RolegroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roleGroup")
@RequiredArgsConstructor
public class RoleGroupController {
    private final RolegroupService rolegroupService;

    //get all Role group
    @GetMapping
    public ResponseEntity<List<RolegroupResponse.getRoleGroupResponse>> findAll(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(rolegroupService.getAllRoleGroup(keyword));
    }

    //get role group by Id
    @GetMapping(headers = "idRoleGroupSearch")
    public ResponseEntity<RolegroupResponse.getRoleGroupResponse> getRoleById(
            @RequestHeader("idRoleGroupSearch") Integer roleGID) {
        return ResponseEntity.ok(rolegroupService.getRoleById(roleGID));
    }

    //add role group
    @PostMapping
    public ResponseEntity<RolegroupResponse.getRoleGroupResponse> addRoleGroup(
            @Valid @RequestBody RolegroupRequest.roleGroupAddRequest roleGroupadd) {
        return ResponseEntity.ok(rolegroupService.addRoleGroup(roleGroupadd));
    }

    //Update Role group
    @PutMapping
    public ResponseEntity<RolegroupResponse.roleGroupUpdateResponse> updateRole(
            @RequestParam Integer id,
            @Valid
            @RequestBody RolegroupRequest.roleGroupUpdateRequest roleGUpdate
    ) {
        rolegroupService.updateRoleGroup(id, roleGUpdate.roleId, roleGUpdate.namaGroupRole);

        RolegroupResponse.roleGroupUpdateResponse respUpdate = new RolegroupResponse.roleGroupUpdateResponse();
        respUpdate.setMessage("Role Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    @DeleteMapping
    public ResponseEntity<RolegroupResponse.roleGroupDeleteResponse> deleteRoleGroup(
            @Valid @RequestParam Integer Id) {
        rolegroupService.deleteRoleGroup(Id);

        RolegroupResponse.roleGroupDeleteResponse respDelete = new RolegroupResponse.roleGroupDeleteResponse();
        respDelete.setMessage("Delete role Group successfully");
        return ResponseEntity.ok(respDelete);
    }

    //list employee yang ada di dalam satu role group
    @GetMapping("/employees")
    public ResponseEntity<Page<RolegroupResponse.roleGroupMemberResponse>> getMembers(
            @RequestParam Integer roleGroupId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(rolegroupService.getMembers(roleGroupId, keyword, page, size));
    }

    @GetMapping("/employees/add")
    public ResponseEntity<List<EmployeResponse.employeGetResponse>> getEmployeeNonRg() {
        List<EmployeResponse.employeGetResponse> employeesNonRg = rolegroupService.findEmployeesNonRG();
        return ResponseEntity.ok(employeesNonRg);
    }

    //assign employee ke role group
    @PostMapping("/employees")
    public ResponseEntity<Void> assignEmployee(
            @RequestParam Integer roleGroupId,
            @Valid @RequestBody RolegroupRequest.assignEmployeeRequest request) {
        rolegroupService.assignEmployee(roleGroupId, request.employeeId);
        return ResponseEntity.ok().build();
    }

    //keluarkan employee dari role group
    @DeleteMapping("/employees")
    public ResponseEntity<Void> removeEmployee(
            @RequestParam Integer roleGroupId,
            @RequestParam Integer employeeId) {
        rolegroupService.removeEmployee(roleGroupId, employeeId);
        return ResponseEntity.ok().build();
    }

}
