package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.EmployeRequest;
import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.service.EmployeService;
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

@Tag(name = "Employee", description = "Kelola data karyawan (staff internal). Semua endpoint hanya bisa diakses oleh SUPERADMIN.")
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeController {
    private final EmployeService employeService;

    //get all employee with pagination
    @Operation(summary = "Ambil semua employee (paginated)", description = "Mengembalikan daftar karyawan dengan pagination, bisa difilter dengan keyword. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar employee", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeResponse.employeGetResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<EmployeResponse.employeGetResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(employeService.findAll(keyword, page, size));
    }

    //findbyusername by headers di postmannya
    @Operation(summary = "Cari employee berdasarkan nama", description = "Mencari satu data employee berdasarkan username yang dikirim lewat header namaYangDicari. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeResponse.employeGetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header namaYangDicari tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'namaYangDicari' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Employee dengan nama tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "employee dengan username budi tidak ditemukan")))
    })
    @GetMapping(headers = "namaYangDicari")
    public ResponseEntity<EmployeResponse.employeGetResponse> findByName(
            @Parameter(description = "Username employee yang dicari, dikirim lewat header namaYangDicari") @RequestHeader("namaYangDicari") String name) {
        return ResponseEntity.ok(employeService.getEmployeeServiceUserName(name));
    }

    //add employee
    @Operation(summary = "Tambah employee", description = "Membuat akun karyawan baru. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeResponse.employeAddResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "email harus terisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "409", description = "Email/NIP/username sudah terdaftar (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PostMapping
    public ResponseEntity<EmployeResponse.employeAddResponse> addEmployee(
            @Valid @RequestBody EmployeRequest.employeAddRequest employe) {
        return ResponseEntity.ok(employeService.addEmploye(employe));
    }

    @Operation(summary = "Update email & password employee", description = "Memperbarui email dan password employee berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email dan password berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeResponse.employeUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Email dan Password Berhasil di Update!\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "password harus terisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Employee dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "employee dengan Id 3 tidak ditemukan"))),
            @ApiResponse(responseCode = "409", description = "Email sudah dipakai employee lain (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PutMapping
    public ResponseEntity<EmployeResponse.employeUpdateResponse> updateEmployee(
            @RequestParam Integer id,
            @Valid
            @RequestBody EmployeRequest.employeChangeCredentialRequest request
    ) {
        employeService.updateEmployeEmailPass(id, request.email, request.password);

        EmployeResponse.employeUpdateResponse respUpdate = new EmployeResponse.employeUpdateResponse();
        respUpdate.setMessage("Email dan Password Berhasil di Update!");
        return ResponseEntity.ok(respUpdate);
    }

    @Operation(summary = "Hapus employee", description = "Menghapus data karyawan berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeResponse.employeDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete employee successfully\", \"status\": \"Successfuly Deleted\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Employee dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "employee dengan Id 3 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<EmployeResponse.employeDeleteResponse> deleteEmployee(@RequestParam Integer id) {
        employeService.deleteEmployee(id);

        EmployeResponse.employeDeleteResponse respDelete = new EmployeResponse.employeDeleteResponse();
        respDelete.setMessage("Delete employee successfully");
        respDelete.setStatus("Successfuly Deleted");
        return ResponseEntity.ok(respDelete);
    }
}
