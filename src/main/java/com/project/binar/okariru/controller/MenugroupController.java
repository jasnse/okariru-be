package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;
import com.project.binar.okariru.service.MenugroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menugroup")
@RequiredArgsConstructor
public class MenugroupController {
    private final MenugroupService menugroupService;

    //get all menu group, atau filter+search+pagination berdasarkan roleGroupId kalau di-isi
    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer roleGroupId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (roleGroupId != null) {
            return ResponseEntity.ok(menugroupService.getMenuGroupsByRoleGroup(roleGroupId, keyword, page, size));
        }
        return ResponseEntity.ok(menugroupService.getAllMenuGroup());
    }

    //list menu yang belum di-assign ke role group tertentu (buat dropdown "Add Menu")
    @GetMapping("/add")
    public ResponseEntity<List<MenuResponse.getMenuResponse>> getMenusNotInRoleGroup(
            @RequestParam Integer roleGroupId) {
        return ResponseEntity.ok(menugroupService.getMenusNotInRoleGroup(roleGroupId));
    }

    //get menu group by Id
    @GetMapping(headers = "idMenuGroupSearch")
    public ResponseEntity<MenugroupResponse.getMenuGroupResponse> getMenuGroupById(
            @Valid @RequestHeader("idMenuGroupSearch") Integer id) {
        return ResponseEntity.ok(menugroupService.getMenuGroupById(id));
    }

    //add menu group
    @PostMapping
    public ResponseEntity<MenugroupResponse.getMenuGroupResponse> addMenuGroup(
            @Valid @RequestBody MenugroupRequest.menuGroupAddRequest request) {
        return ResponseEntity.ok(menugroupService.addMenuGroup(request));
    }

    //update menu group
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
    @DeleteMapping
    public ResponseEntity<MenugroupResponse.menuGroupDeleteResponse> deleteMenuGroup(
            @Valid @RequestParam Integer Id) {
        menugroupService.deleteMenuGroup(Id);

        MenugroupResponse.menuGroupDeleteResponse respDelete = new MenugroupResponse.menuGroupDeleteResponse();
        respDelete.setMessage("Delete menu group successfully");
        return ResponseEntity.ok(respDelete);
    }
}
