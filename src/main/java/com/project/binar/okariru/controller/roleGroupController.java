package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.RoleRequest;
import com.project.binar.okariru.dto.RoleResponse;
import com.project.binar.okariru.dto.RolegroupRequest;
import com.project.binar.okariru.dto.RolegroupResponse;
import com.project.binar.okariru.service.RolegroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roleGroup")
@RequiredArgsConstructor
public class RoleGroupController {
    private final RolegroupService rolegroupService;

    //get all Role group
    @GetMapping
    public ResponseEntity<List<RolegroupResponse.getRoleGroupResponse>> findAll() {
        return ResponseEntity.ok(rolegroupService.getAllRoleGroup());
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
        rolegroupService.updateRoleGroup(id, roleGUpdate.roleId, roleGUpdate.employeeId, roleGUpdate.namaGroupRole);

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

}
