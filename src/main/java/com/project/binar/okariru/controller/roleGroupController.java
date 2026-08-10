package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.dto.rolegroupRequest;
import com.project.binar.okariru.dto.rolegroupResponse;
import com.project.binar.okariru.service.rolegroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roleGroup")
@RequiredArgsConstructor
public class roleGroupController {
    private final rolegroupService rolegroupService;

    //get all Role group
    @GetMapping
    public ResponseEntity<List<rolegroupResponse.getRoleGroupResponse>> findAll() {
        return ResponseEntity.ok(rolegroupService.getAllRoleGroup());
    }

    //get role group by Id
    @GetMapping(headers = "idRoleGroupSearch")
    public ResponseEntity<rolegroupResponse.getRoleGroupResponse> getRoleById(
            @RequestHeader("idRoleGroupSearch") Integer roleGID) {
        return ResponseEntity.ok(rolegroupService.getRoleById(roleGID));
    }

    //add role group
    @PostMapping
    public ResponseEntity<rolegroupResponse.getRoleGroupResponse> addRoleGroup(
            @Valid @RequestBody rolegroupRequest.roleGroupAddRequest roleGroupadd) {
        return ResponseEntity.ok(rolegroupService.addRoleGroup(roleGroupadd));
    }

    //Update Role group
    @PutMapping
    public ResponseEntity<rolegroupResponse.roleGroupUpdateResponse> updateRole(
            @RequestParam Integer id,
            @Valid
            @RequestBody rolegroupRequest.roleGroupUpdateRequest roleGUpdate
    ) {
        rolegroupService.updateRoleGroup(id, roleGUpdate.roleId, roleGUpdate.employeeId, roleGUpdate.namaGroupRole);

        rolegroupResponse.roleGroupUpdateResponse respUpdate = new rolegroupResponse.roleGroupUpdateResponse();
        respUpdate.setMessage("Role Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    @DeleteMapping
    public ResponseEntity<rolegroupResponse.roleGroupDeleteResponse> deleteRoleGroup(
            @Valid @RequestParam Integer Id) {
        rolegroupService.deleteRoleGroup(Id);

        rolegroupResponse.roleGroupDeleteResponse respDelete = new rolegroupResponse.roleGroupDeleteResponse();
        respDelete.setMessage("Delete role Group successfully");
        return ResponseEntity.ok(respDelete);
    }

}
