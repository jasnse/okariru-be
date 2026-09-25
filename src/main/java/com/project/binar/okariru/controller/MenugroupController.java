package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;
import com.project.binar.okariru.service.MenugroupService;
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

@Tag(name = "Menu Group", description = "Kelola pemetaan menu ke role group (menu apa saja yang dimiliki satu role group). Semua endpoint hanya bisa diakses oleh SUPERADMIN.")
@RestController
@RequestMapping("/api/v1/menugroup")
@RequiredArgsConstructor
public class MenugroupController {
    private final MenugroupService menugroupService;

    //get all menu group, atau filter+search+pagination berdasarkan roleGroupId kalau di-isi
    @Operation(summary = "Ambil semua menu group", description = "Mengembalikan seluruh menu group, atau bila roleGroupId diisi mengembalikan menu group milik role group tersebut (dengan filter keyword + pagination). Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar menu group", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenugroupResponse.getMenuGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<?> findAll(
            @Parameter(description = "Jika diisi, hasil difilter berdasarkan role group ini (dengan pagination); jika kosong, mengembalikan semua menu group") @RequestParam(required = false) Integer roleGroupId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (roleGroupId != null) {
            return ResponseEntity.ok(menugroupService.getMenuGroupsByRoleGroup(roleGroupId, keyword, page, size));
        }
        return ResponseEntity.ok(menugroupService.getAllMenuGroup());
    }

    //list menu yang belum di-assign ke role group tertentu (buat dropdown "Add Menu")
    @Operation(summary = "Ambil menu yang belum di-assign ke role group", description = "Mengembalikan daftar menu yang belum dimiliki oleh role group tertentu, dipakai untuk dropdown \"Add Menu\" di UI. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar menu", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuResponse.getMenuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter roleGroupId tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'roleGroupId' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping("/add")
    public ResponseEntity<List<MenuResponse.getMenuResponse>> getMenusNotInRoleGroup(
            @RequestParam Integer roleGroupId) {
        return ResponseEntity.ok(menugroupService.getMenusNotInRoleGroup(roleGroupId));
    }

    //get menu group by Id
    @Operation(summary = "Ambil menu group berdasarkan Id", description = "Mencari satu data menu group berdasarkan Id yang dikirim lewat header idMenuGroupSearch. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu group ditemukan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenugroupResponse.getMenuGroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Header idMenuGroupSearch tidak dikirim atau tidak valid", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Header 'idMenuGroupSearch' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Menu group dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "menu group dengan Id 1 tidak ditemukan")))
    })
    @GetMapping(headers = "idMenuGroupSearch")
    public ResponseEntity<MenugroupResponse.getMenuGroupResponse> getMenuGroupById(
            @Parameter(description = "Id menu group yang dicari, dikirim lewat header idMenuGroupSearch") @Valid @RequestHeader("idMenuGroupSearch") Integer id) {
        return ResponseEntity.ok(menugroupService.getMenuGroupById(id));
    }

    //add menu group
    @Operation(summary = "Tambah menu group", description = "Menambahkan sebuah menu ke sebuah role group. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu group berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenugroupResponse.getMenuGroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "menu id harus di pilih"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Menu atau role group tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "menu dengan Id 2 tidak ditemukan"))),
            @ApiResponse(responseCode = "409", description = "Menu sudah pernah di-assign ke role group ini (duplikat)", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Data sudah terdaftar (duplikat)")))
    })
    @PostMapping
    public ResponseEntity<MenugroupResponse.getMenuGroupResponse> addMenuGroup(
            @Valid @RequestBody MenugroupRequest.menuGroupAddRequest request) {
        return ResponseEntity.ok(menugroupService.addMenuGroup(request));
    }

    //update menu group
    @Operation(summary = "Update menu group", description = "Memperbarui pemetaan menu group (menu dan/atau role group) berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu group berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenugroupResponse.menuGroupUpdateResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Menu Group Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "role group id harus di pilih"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Menu group, menu, atau role group tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "menu group dengan Id 1 tidak ditemukan")))
    })
    @PutMapping
    public ResponseEntity<MenugroupResponse.menuGroupUpdateResponse> updateMenuGroup(
            @Valid
            @RequestParam Integer id,
            @Valid @RequestBody MenugroupRequest.menuGroupUpdateRequest request
    ) {
        menugroupService.updateMenuGroup(id, request.menuId, request.roleGroupId);

        MenugroupResponse.menuGroupUpdateResponse respUpdate = new MenugroupResponse.menuGroupUpdateResponse();
        respUpdate.setMessage("Menu Group Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete menu group
    @Operation(summary = "Hapus menu group", description = "Menghapus pemetaan menu group berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu group berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenugroupResponse.menuGroupDeleteResponse.class), examples = @ExampleObject(value = "{\"Message\": \"Delete menu group successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Menu group dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "menu group dengan Id 1 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<MenugroupResponse.menuGroupDeleteResponse> deleteMenuGroup(
            @Valid @RequestParam Integer Id) {
        menugroupService.deleteMenuGroup(Id);

        MenugroupResponse.menuGroupDeleteResponse respDelete = new MenugroupResponse.menuGroupDeleteResponse();
        respDelete.setMessage("Delete menu group successfully");
        return ResponseEntity.ok(respDelete);
    }
}
