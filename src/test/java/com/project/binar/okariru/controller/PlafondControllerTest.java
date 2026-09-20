package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.PlafondRequest;
import com.project.binar.okariru.dto.PlafondResponse;
import com.project.binar.okariru.service.PlafondService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlafondControllerTest {

    @Mock
    private PlafondService plafondService;

    @InjectMocks
    private PlafondController controller;

    private PlafondResponse.getPlafondResponse response() {
        return new PlafondResponse.getPlafondResponse(1, 7, 10_000, 6_000L, "desk", 3, null, null, null);
    }

    @Test
    void findAll_mengembalikanDaftarDariService() {
        when(plafondService.getAllPlafond()).thenReturn(List.of(response()));

        ResponseEntity<List<PlafondResponse.getPlafondResponse>> result = controller.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getPlafondByCustomerId_meneruskanUserId() {
        when(plafondService.getPlafondByCustomerId(7)).thenReturn(response());

        ResponseEntity<PlafondResponse.getPlafondResponse> result = controller.getPlafondByCustomerId(7);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(7, result.getBody().getUserId());
    }

    @Test
    void getPlafondById_meneruskanId() {
        when(plafondService.getPlafondById(1)).thenReturn(response());

        ResponseEntity<PlafondResponse.getPlafondResponse> result = controller.getPlafondById(1);

        assertEquals(1, result.getBody().getPlafondId());
    }

    @Test
    void addPlafond_mengembalikanHasilService() {
        PlafondRequest.plafondAddRequest req = new PlafondRequest.plafondAddRequest();
        when(plafondService.addPlafond(req)).thenReturn(response());

        ResponseEntity<PlafondResponse.getPlafondResponse> result = controller.addPlafond(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(10_000, result.getBody().getTotalPlafond());
    }

    @Test
    void updatePlafond_memanggilServiceDanMengembalikanPesan() {
        PlafondRequest.plafondUpdateRequest req = new PlafondRequest.plafondUpdateRequest();
        req.userId = 7;
        req.totalPlafond = 20_000;
        req.deskripsiPlafond = "Naik";
        req.updatedBy = 3;

        ResponseEntity<PlafondResponse.plafondUpdateResponse> result = controller.updatePlafond(1, req);

        verify(plafondService).updatePlafond(1, 7, 20_000, "Naik", 3);
        assertEquals("Plafond Berhasil di update", result.getBody().getMessage());
    }

    @Test
    void deletePlafond_memanggilServiceDanMengembalikanPesan() {
        ResponseEntity<PlafondResponse.plafondDeleteResponse> result = controller.deletePlafond(1);

        verify(plafondService).deletePlafond(1);
        assertEquals("Delete plafond successfully", result.getBody().getMessage());
    }
}
