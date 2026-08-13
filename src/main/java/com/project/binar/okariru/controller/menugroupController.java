package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;
import com.project.binar.okariru.service.MenugroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menugroup")
@RequiredArgsConstructor
public class MenugroupController {
    private final MenugroupService menugroupService;

    //get all menu group
    @GetMapping
    public ResponseEntity<List<MenugroupResponse.getMenuGroupResponse>> findAll() {
        return ResponseEntity.ok(menugroupService.getAllMenuGroup());
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
        menugroupService.updateMenuGroup(id, request.menuId, request.roleGroupId, request.namaGroupMenu);

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
