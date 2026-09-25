package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Master Data Menu", description = "Kelola master data menu aplikasi (menu master, bukan menu group)")
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    //Get all menu
//    @GetMapping
//    public ResponseEntity<List<MenuResponse.getMenuResponse>> findAll() {
//        return ResponseEntity.ok(menuService.getAllmenuService());
//    }

    //get menu By ID
//    @GetMapping(headers = "idMenuSearch")
//    public ResponseEntity<MenuResponse.getMenuResponse> findByMenuId(@RequestHeader("idMenuSearch") Integer menuId) {
//        return ResponseEntity.ok(menuService.getmenuById(menuId));
//    }

    //get all Menu with pagination
    @Operation(summary = "Ambil semua menu (paginated)", description = "Mengembalikan daftar menu master dengan pagination, bisa difilter dengan keyword. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar menu", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuResponse.getMenuResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<MenuResponse.getMenuResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(menuService.findAll(keyword, page, size));
    }

    //add Menu
    @Operation(summary = "Tambah menu", description = "Membuat data menu master baru. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu berhasil ditambahkan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuResponse.getMenuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Nama menu gak boleh kosong"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @PostMapping
    public ResponseEntity<MenuResponse.getMenuResponse> addMenu(
            @Valid @RequestBody MenuRequest.menuAddRequest menuAdd) {
        return ResponseEntity.ok(menuService.addMenu(menuAdd));
    }

    // update menu
    @Operation(summary = "Update menu", description = "Memperbarui data menu master berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu berhasil diupdate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuResponse.menuUpdateResponse.class), examples = @ExampleObject(value = "{\"message\": \"Role Berhasil di update\"}"))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal / parameter id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "deskripsi Menu gak boleh kosong"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Menu dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "menu dengan Id 2 tidak ditemukan")))
    })
    @PutMapping
    public ResponseEntity<MenuResponse.menuUpdateResponse> updateMenu(
            @RequestParam Integer id,
            @Valid
            @RequestBody MenuRequest.menuUpdateRequest menuUpdateRequest
    ) {
        menuService.updatemenu(id, menuUpdateRequest.namaMenu, menuUpdateRequest.deskripsiMenu,
                menuUpdateRequest.path, menuUpdateRequest.icon);

        MenuResponse.menuUpdateResponse respUpdate = new MenuResponse.menuUpdateResponse();
        respUpdate.setMessage("Role Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    @Operation(summary = "Hapus menu", description = "Menghapus data menu master berdasarkan Id. Hanya bisa diakses oleh SUPERADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu berhasil dihapus", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuResponse.menuDeleteResponse.class), examples = @ExampleObject(value = "{\"message\": \"Delete role successfully\", \"status\": \"Successfuly Deleted\"}"))),
            @ApiResponse(responseCode = "400", description = "Parameter Id tidak dikirim", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Parameter 'Id' wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses (hanya SUPERADMIN)", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}"))),
            @ApiResponse(responseCode = "404", description = "Menu dengan Id tersebut tidak ditemukan", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "menu dengan Id 2 tidak ditemukan")))
    })
    @DeleteMapping
    public ResponseEntity<MenuResponse.menuDeleteResponse> deleteRole(@RequestParam Integer Id) {
        menuService.deleteMenu(Id);

        MenuResponse.menuDeleteResponse respDelete = new MenuResponse.menuDeleteResponse();
        respDelete.setMessage("Delete role successfully");
        respDelete.setStatus("Successfuly Deleted");
        return ResponseEntity.ok(respDelete);
    }

    @Operation(summary = "Ambil menu milik user yang login", description = "Mengembalikan daftar menu yang boleh diakses oleh employee sesuai role group-nya, dipakai untuk membangun sidebar aplikasi. Bisa diakses SUPERADMIN, MARKETING, BRANCH_MANAGER, atau BACKOFFICE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar menu milik user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuResponse.myMenuResponse.class))),
            @ApiResponse(responseCode = "401", description = "Belum login / token tidak valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Unauthorized - silakan login terlebih dahulu\"}"))),
            @ApiResponse(responseCode = "403", description = "Role tidak memiliki akses", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}")))
    })
    @GetMapping("/my-menu")
    public ResponseEntity<List<MenuResponse.myMenuResponse>> getMyMenu(Authentication authentication) {
        return ResponseEntity.ok(menuService.getMyMenu(authentication.getName()));
    }
}
