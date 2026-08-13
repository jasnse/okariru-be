package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PlafondRequest;
import com.project.binar.okariru.dto.PlafondResponse;
import com.project.binar.okariru.service.PlafondService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plafond")
@RequiredArgsConstructor
public class PlafondController {
    private final PlafondService plafondService;

    //get all plafond
    @GetMapping
    public ResponseEntity<List<PlafondResponse.getPlafondResponse>> findAll() {
        return ResponseEntity.ok(plafondService.getAllPlafond());
    }

    //get plafond by Id
    @GetMapping(headers = "idPlafondSearch")
    public ResponseEntity<PlafondResponse.getPlafondResponse> getPlafondById(
            @RequestHeader("idPlafondSearch") Integer id) {
        return ResponseEntity.ok(plafondService.getPlafondById(id));
    }

    //add plafond
    @PostMapping
    public ResponseEntity<PlafondResponse.getPlafondResponse> addPlafond(
            @Valid @RequestBody PlafondRequest.plafondAddRequest request) {
        return ResponseEntity.ok(plafondService.addPlafond(request));
    }

    //update plafond
    @PutMapping
    public ResponseEntity<PlafondResponse.plafondUpdateResponse> updatePlafond(
            @RequestParam Integer id,
            @Valid @RequestBody PlafondRequest.plafondUpdateRequest request
    ) {
        plafondService.updatePlafond(id, request.userId, request.totalPlafond, request.deskripsiPlafond, request.updatedBy);

        PlafondResponse.plafondUpdateResponse respUpdate = new PlafondResponse.plafondUpdateResponse();
        respUpdate.setMessage("Plafond Berhasil di update");
        return ResponseEntity.ok(respUpdate);
    }

    //delete plafond
    @DeleteMapping
    public ResponseEntity<PlafondResponse.plafondDeleteResponse> deletePlafond(@RequestParam Integer Id) {
        plafondService.deletePlafond(Id);

        PlafondResponse.plafondDeleteResponse respDelete = new PlafondResponse.plafondDeleteResponse();
        respDelete.setMessage("Delete plafond successfully");
        return ResponseEntity.ok(respDelete);
    }
}
