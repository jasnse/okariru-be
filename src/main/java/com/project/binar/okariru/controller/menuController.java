package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.RoleRequest;
import com.project.binar.okariru.dto.RoleResponse;
import com.project.binar.okariru.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.awt.SystemColor.menu;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    //Get all menu
    @GetMapping
    public ResponseEntity<List<MenuResponse.getMenuResponse>> findAll() {
        return ResponseEntity.ok(menuService.getAllmenuService());
    }

    //get menu By ID
    @GetMapping(headers = "idMenuSearch")
    public ResponseEntity<MenuResponse.getMenuResponse> findByMenuId(@RequestHeader("idMenuSearch") Integer menuId) {
        return ResponseEntity.ok(menuService.getmenuById(menuId));
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
        menuService.updatemenu(id, menuUpdateRequest.namaMenu, menuUpdateRequest.deskripsiMenu);

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
}
