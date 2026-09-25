package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.RoleRequest;
import com.project.binar.okariru.dto.RoleResponse;
import com.project.binar.okariru.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Role", description = "Kelola master data role (jabatan/peran) karyawan. Semua endpoint hanya bisa diakses oleh SUPERADMIN.")
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

    @Operation(summary = "Ambil semua role (paginated)", description = "Mengembalikan daftar role dengan pagination, bisa difilter dengan keyword. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar role", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.getRoleResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<RoleResponse.getRoleResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(roleService.findAll(keyword, page, size));
    }

    //get role by id
    @Operation(summary = "Ambil role berdasarkan Id", description = "Mencari satu data role berdasarkan Id yang dikirim lewat header idRoleSearch. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.getRoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idRoleSearch tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idRoleSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Role dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "role dengan Id 1 tidak ditemukan")))
    })
    @GetMapping(headers = "idRoleSearch")
    public ResponseEntity<RoleResponse.getRoleResponse> findByroleId(
             @Parameter(description = "Id role yang dicari, dikirim lewat header idRoleSearch") @RequestHeader("idRoleSearch") Integer roleID) {
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
