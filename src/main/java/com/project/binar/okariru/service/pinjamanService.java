package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.PinjamanRequest;
import com.project.binar.okariru.dto.PinjamanResponse;

import java.util.List;

public interface PinjamanService {

    List<PinjamanResponse.getPinjamanResponse> getAllPinjaman();

    PinjamanResponse.getPinjamanResponse getPinjamanById(Integer id);

    PinjamanResponse.getPinjamanResponse addPinjaman(PinjamanRequest.pinjamanAddRequest addRequest);

    void updatePinjaman(Integer id, String jenisPinjaman, String deskripsiPinjaman, Double bunga, Double biayaLainnya);

    String deletePinjaman(Integer id);
}
