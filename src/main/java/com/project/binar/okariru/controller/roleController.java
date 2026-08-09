package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.service.roleService;
import jakarta.validation.Valid;
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
        return ResponseEntity.ok(roleService.getAllRoleService());
    }

    //get role by Id
    @GetMapping(headers = "idRoleSearch")
    public ResponseEntity<roleResponse.getRoleResponse> findByroleId(
             @RequestHeader("idRoleSearch") Integer roleID) {
        return ResponseEntity.ok(roleService.getRoleById(roleID));
    }

    //add role
    @PostMapping
    public ResponseEntity<roleResponse.getRoleResponse> addRole(
            @Valid @RequestBody roleRequest.roleAddRequest roleadd) {
        return ResponseEntity.ok(roleService.addRole(roleadd));
    }

    //Update Role
    @PutMapping
    public ResponseEntity<roleResponse.roleUpdateResponse> updateRole(
            @RequestParam Integer id,
            @Valid
            @RequestBody roleRequest.roleUpdateRequest roleupdate
    ) {
        roleService.updateRole(id, roleupdate.nama_role);

        roleResponse.roleUpdateResponse respUpdate = new roleResponse.roleUpdateResponse();
        respUpdate.setMessage("Role Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //Delete Role
    @DeleteMapping
    public ResponseEntity<roleResponse.roleDeleteResponse> deleteRole(@RequestParam Integer Id) {
        roleService.deleteRole(Id);

        roleResponse.roleDeleteResponse respDelete = new roleResponse.roleDeleteResponse();
        respDelete.setMessage("Delete role successfully");
        respDelete.setStatus("Successfuly Deleted");
        return ResponseEntity.ok(respDelete);
    }
}
