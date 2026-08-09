package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.menuRequest;
import com.project.binar.okariru.dto.menuResponse;
import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.service.menuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.awt.SystemColor.menu;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class menuController {

    private final menuService menuService;

    //Get all menu
    @GetMapping
    public ResponseEntity<List<menuResponse.getMenuResponse>> findAll() {
        return ResponseEntity.ok(menuService.getAllmenuService());
    }

    //get menu By ID
    @GetMapping(headers = "idMenuSearch")
    public ResponseEntity<menuResponse.getMenuResponse> findByMenuId(@RequestHeader("idMenuSearch") Integer menuId) {
        return ResponseEntity.ok(menuService.getmenuById(menuId));
    }

    //add Menu
    @PostMapping
    public ResponseEntity<menuResponse.getMenuResponse> addMenu(
            @Valid @RequestBody menuRequest.menuAddRequest menuAdd) {
        return ResponseEntity.ok(menuService.addMenu(menuAdd));
    }

    // update menu
    @PutMapping
    public ResponseEntity<menuResponse.menuUpdateResponse> updateMenu(
            @RequestParam Integer id,
            @Valid
            @RequestBody menuRequest.menuUpdateRequest menuUpdateRequest
    ) {
        menuService.updatemenu(id, menuUpdateRequest.namaMenu, menuUpdateRequest.deskripsiMenu);

        menuResponse.menuUpdateResponse respUpdate = new menuResponse.menuUpdateResponse();
        respUpdate.setMessage("Role Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    @DeleteMapping
    public ResponseEntity<menuResponse.menuDeleteResponse> deleteRole(@RequestParam Integer Id) {
        menuService.deleteMenu(Id);

        menuResponse.menuDeleteResponse respDelete = new menuResponse.menuDeleteResponse();
        respDelete.setMessage("Delete role successfully");
        respDelete.setStatus("Successfuly Deleted");
        return ResponseEntity.ok(respDelete);
    }
}
