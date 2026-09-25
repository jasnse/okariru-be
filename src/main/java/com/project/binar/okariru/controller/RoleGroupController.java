package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.*;
import com.project.binar.okariru.service.RolegroupService;
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

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Role Group", description = "Kelola role group (grup hak akses) beserta anggota karyawannya. Semua endpoint hanya bisa diakses oleh SUPERADMIN.")
@RestController
@RequestMapping("/api/v1/roleGroup")
@RequiredArgsConstructor
public class RoleGroupController {
    private final RolegroupService rolegroupService;

    //get all Role group
    @Operation(summary = "Ambil semua role group", description = "Mengembalikan seluruh data role group, bisa difilter dengan keyword. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar role group", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolegroupResponse.getRoleGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<List<RolegroupResponse.getRoleGroupResponse>> findAll(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(rolegroupService.getAllRoleGroup(keyword));
    }

    //get role group by Id
    @Operation(summary = "Ambil role group berdasarkan Id", description = "Mencari satu data role group berdasarkan Id yang dikirim lewat header idRoleGroupSearch. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role group ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolegroupResponse.getRoleGroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idRoleGroupSearch tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idRoleGroupSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Role group dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "role group dengan Id 1 tidak ditemukan")))
    })
    @GetMapping(headers = "idRoleGroupSearch")
    public ResponseEntity<RolegroupResponse.getRoleGroupResponse> getRoleById(
            @Parameter(description = "Id role group yang dicari, dikirim lewat header idRoleGroupSearch") @RequestHeader("idRoleGroupSearch") Integer roleGID) {
        return ResponseEntity.ok(rolegroupService.getRoleById(roleGID));
    }

    //add role group
    @Operation(summary = "Tambah role group", description = "Membuat data role group baru. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role group berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolegroupResponse.getRoleGroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "nama group role gak boleh kosong"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Role (roleId) tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "role dengan Id 1 tidak ditemukan"))),
            @ApiResponse(responseCode = "409", description = "Nama group role sudah terdaftar (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PostMapping
    public ResponseEntity<RolegroupResponse.getRoleGroupResponse> addRoleGroup(
            @Valid @RequestBody RolegroupRequest.roleGroupAddRequest roleGroupadd) {
        return ResponseEntity.ok(rolegroupService.addRoleGroup(roleGroupadd));
    }

    //Update Role group
    @Operation(summary = "Update role group", description = "Memperbarui data role group berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role group berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolegroupResponse.roleGroupUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Role Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "nama Group Role harus di pilih"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Role group atau role (roleId) tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "role group dengan Id 1 tidak ditemukan"))),
            @ApiResponse(responseCode = "409", description = "Nama group role sudah dipakai role group lain (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
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

    @Operation(summary = "Hapus role group", description = "Menghapus data role group berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role group berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolegroupResponse.roleGroupDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete role Group successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Role group dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "role group dengan Id 1 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<RolegroupResponse.roleGroupDeleteResponse> deleteRoleGroup(
            @Valid @RequestParam Integer Id) {
        rolegroupService.deleteRoleGroup(Id);

        RolegroupResponse.roleGroupDeleteResponse respDelete = new RolegroupResponse.roleGroupDeleteResponse();
        respDelete.setMessage("Delete role Group successfully");
        return ResponseEntity.ok(respDelete);
    }

    //list employee yang ada di dalam satu role group
    @Operation(summary = "Ambil anggota role group (paginated)", description = "Mengembalikan daftar employee yang menjadi anggota satu role group, dengan pagination dan filter keyword. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar anggota", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RolegroupResponse.roleGroupMemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter roleGroupId tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'roleGroupId' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping("/employees")
    public ResponseEntity<Page<RolegroupResponse.roleGroupMemberResponse>> getMembers(
            @RequestParam Integer roleGroupId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(rolegroupService.getMembers(roleGroupId, keyword, page, size));
    }

    @Operation(summary = "Ambil employee yang belum punya role group", description = "Mengembalikan daftar employee yang belum menjadi anggota role group manapun, dipakai untuk dropdown \"Add Employee\". Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar employee", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeResponse.employeGetResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping("/employees/add")
    public ResponseEntity<List<EmployeResponse.employeGetResponse>> getEmployeeNonRg() {
        List<EmployeResponse.employeGetResponse> employeesNonRg = rolegroupService.findEmployeesNonRG();
        return ResponseEntity.ok(employeesNonRg);
    }

    //assign employee ke role group
    @Operation(summary = "Assign employee ke role group", description = "Menambahkan seorang employee sebagai anggota role group tertentu. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee berhasil di-assign ke role group (response kosong)"),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter roleGroupId tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "employee_id harus diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Role group atau employee tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "employee dengan Id 3 tidak ditemukan"))),
            @ApiResponse(responseCode = "409", description = "Employee sudah menjadi anggota role group (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PostMapping("/employees")
    public ResponseEntity<Void> assignEmployee(
            @RequestParam Integer roleGroupId,
            @Valid @RequestBody RolegroupRequest.assignEmployeeRequest request) {
        rolegroupService.assignEmployee(roleGroupId, request.employeeId);
        return ResponseEntity.ok().build();
    }

    //keluarkan employee dari role group
    @Operation(summary = "Keluarkan employee dari role group", description = "Menghapus keanggotaan seorang employee dari role group tertentu. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee berhasil dikeluarkan dari role group (response kosong)"),
            @ApiResponse(responseCode = "400", description = "Parameter roleGroupId/employeeId tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'employeeId' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Role group atau employee tidak ditemukan / employee bukan anggota role group tersebut", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "employee dengan Id 3 tidak ditemukan")))
    })
    @DeleteMapping("/employees")
    public ResponseEntity<Void> removeEmployee(
            @RequestParam Integer roleGroupId,
            @RequestParam Integer employeeId) {
        rolegroupService.removeEmployee(roleGroupId, employeeId);
        return ResponseEntity.ok().build();
    }

}
