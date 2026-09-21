package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<Page<MenuResponse.getMenuResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(menuService.findAll(keyword, page, size));
    }

    //add Menu
    @PostMapping
    public ResponseEntity<MenuResponse.getMenuResponse> addMenu(
            @Valid @RequestBody MenuRequest.menuAddRequest menuAdd) {
        return ResponseEntity.ok(menuService.addMenu(menuAdd));
    }

    // update menu
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

    @DeleteMapping
    public ResponseEntity<MenuResponse.menuDeleteResponse> deleteRole(@RequestParam Integer Id) {
        menuService.deleteMenu(Id);

        MenuResponse.menuDeleteResponse respDelete = new MenuResponse.menuDeleteResponse();
        respDelete.setMessage("Delete role successfully");
        respDelete.setStatus("Successfuly Deleted");
        return ResponseEntity.ok(respDelete);
    }

    @GetMapping("/my-menu")
    public ResponseEntity<List<MenuResponse.myMenuResponse>> getMyMenu(Authentication authentication) {
        return ResponseEntity.ok(menuService.getMyMenu(authentication.getName()));
    }
}
