package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.menugroupRequest;
import com.project.binar.okariru.dto.menugroupResponse;
import com.project.binar.okariru.service.menugroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menugroup")
@RequiredArgsConstructor
public class menugroupController {
    private final menugroupService menugroupService;

    //get all menu group
    @GetMapping
    public ResponseEntity<List<menugroupResponse.getMenuGroupResponse>> findAll() {
        return ResponseEntity.ok(menugroupService.getAllMenuGroup());
    }

    //get menu group by Id
    @GetMapping(headers = "idMenuGroupSearch")
    public ResponseEntity<menugroupResponse.getMenuGroupResponse> getMenuGroupById(
            @Valid @RequestHeader("idMenuGroupSearch") Integer id) {
        return ResponseEntity.ok(menugroupService.getMenuGroupById(id));
    }

    //add menu group
    @PostMapping
    public ResponseEntity<menugroupResponse.getMenuGroupResponse> addMenuGroup(
            @Valid @RequestBody menugroupRequest.menuGroupAddRequest request) {
        return ResponseEntity.ok(menugroupService.addMenuGroup(request));
    }

    //update menu group
    @PutMapping
    public ResponseEntity<menugroupResponse.menuGroupUpdateResponse> updateMenuGroup(
            @Valid
            @RequestParam Integer id,
            @Valid @RequestBody menugroupRequest.menuGroupUpdateRequest request
    ) {
        menugroupService.updateMenuGroup(id, request.menuId, request.roleGroupId, request.namaGroupMenu);

        menugroupResponse.menuGroupUpdateResponse respUpdate = new menugroupResponse.menuGroupUpdateResponse();
        respUpdate.setMessage("Menu Group Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete menu group
    @DeleteMapping
    public ResponseEntity<menugroupResponse.menuGroupDeleteResponse> deleteMenuGroup(
            @Valid @RequestParam Integer Id) {
        menugroupService.deleteMenuGroup(Id);

        menugroupResponse.menuGroupDeleteResponse respDelete = new menugroupResponse.menuGroupDeleteResponse();
        respDelete.setMessage("Delete menu group successfully");
        return ResponseEntity.ok(respDelete);
    }
}
