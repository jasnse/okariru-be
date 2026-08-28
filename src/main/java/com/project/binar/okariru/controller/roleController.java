package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.RoleRequest;
import com.project.binar.okariru.dto.RoleResponse;
import com.project.binar.okariru.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    //get all Role
//    @GetMapping
//    public ResponseEntity<List<RoleResponse.getRoleResponse>> findAllBase() {
//        return ResponseEntity.ok(roleService.getAllRoleService());
//    }

    @GetMapping
    public ResponseEntity<Page<RoleResponse.getRoleResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(roleService.findAll(keyword, page, size));
    }

    //get role by id
    @GetMapping(headers = "idRoleSearch")
    public ResponseEntity<RoleResponse.getRoleResponse> findByroleId(
             @RequestHeader("idRoleSearch") Integer roleID) {
        return ResponseEntity.ok(roleService.getRoleById(roleID));
    }

    //add role
//    @PostMapping
//    public ResponseEntity<RoleResponse.getRoleResponse> addRole(
//            @Valid @RequestBody RoleRequest.roleAddRequest roleadd) {
//        return ResponseEntity.ok(roleService.addRole(roleadd));
//    }

    //Update Role
//    @PutMapping
//    public ResponseEntity<RoleResponse.roleUpdateResponse> updateRole(
//            @RequestParam Integer id,
//            @Valid
//            @RequestBody RoleRequest.roleUpdateRequest roleupdate
//    ) {
//        roleService.updateRole(id, roleupdate.nama_role);
//
//        RoleResponse.roleUpdateResponse respUpdate = new RoleResponse.roleUpdateResponse();
//        respUpdate.setMessage("Role Berhasil di update");
//        return ResponseEntity.ok(respUpdate);
//    }

    //Delete Role
//    @DeleteMapping
//    public ResponseEntity<RoleResponse.roleDeleteResponse> deleteRole(@RequestParam Integer Id) {
//        roleService.deleteRole(Id);
//
//        RoleResponse.roleDeleteResponse respDelete = new RoleResponse.roleDeleteResponse();
//        respDelete.setMessage("Delete role successfully");
//        respDelete.setStatus("Successfuly Deleted");
//        return ResponseEntity.ok(respDelete);
//    }
}
