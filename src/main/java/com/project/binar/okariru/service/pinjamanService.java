package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.pinjamanRequest;
import com.project.binar.okariru.dto.pinjamanResponse;

import java.util.List;

public interface pinjamanService {

    List<pinjamanResponse.getPinjamanResponse> getAllPinjaman();

    pinjamanResponse.getPinjamanResponse getPinjamanById(Integer id);

    pinjamanResponse.getPinjamanResponse addPinjaman(pinjamanRequest.pinjamanAddRequest addRequest);

    void updatePinjaman(Integer id, String jenisPinjaman, String deskripsiPinjaman, Double bunga, Double biayaLainnya);

    String deletePinjaman(Integer id);
}
