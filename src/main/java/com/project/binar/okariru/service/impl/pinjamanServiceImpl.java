package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.pinjamanRequest;
import com.project.binar.okariru.dto.pinjamanResponse;
import com.project.binar.okariru.entity.pinjamanEntity;
import com.project.binar.okariru.repository.pinjamanRepository;
import com.project.binar.okariru.service.pinjamanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class pinjamanServiceImpl implements pinjamanService {

    private final pinjamanRepository pinjamanRepository;

    @Override
    public List<pinjamanResponse.getPinjamanResponse> getAllPinjaman() {
        return pinjamanRepository.findAll()
                .stream()
                .map(pinjaman -> new pinjamanResponse.getPinjamanResponse(
                        pinjaman.getPinjamanId(),
                        pinjaman.getJenisPinjaman(),
                        pinjaman.getDeskripsiPinjaman(),
                        pinjaman.getBunga(),
                        pinjaman.getBiayaLainnya(),
                        pinjaman.getCreatedAt(),
                        pinjaman.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public pinjamanResponse.getPinjamanResponse getPinjamanById(Integer id) {
        pinjamanEntity pinjaman = pinjamanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("pinjaman dengan Id " + id + " tidak ditemukan"));
        return new pinjamanResponse.getPinjamanResponse(
                pinjaman.getPinjamanId(),
                pinjaman.getJenisPinjaman(),
                pinjaman.getDeskripsiPinjaman(),
                pinjaman.getBunga(),
                pinjaman.getBiayaLainnya(),
                pinjaman.getCreatedAt(),
                pinjaman.getUpdatedAt()
        );
    }

    @Override
    public pinjamanResponse.getPinjamanResponse addPinjaman(pinjamanRequest.pinjamanAddRequest addRequest) {
        pinjamanEntity pinjaman = new pinjamanEntity();
        pinjaman.setJenisPinjaman(addRequest.jenisPinjaman);
        pinjaman.setDeskripsiPinjaman(addRequest.deskripsiPinjaman);
        pinjaman.setBunga(addRequest.bunga);
        pinjaman.setBiayaLainnya(addRequest.biayaLainnya);
        pinjaman.setCreatedAt(LocalDate.now());

        pinjamanEntity saved = pinjamanRepository.save(pinjaman);
        return new pinjamanResponse.getPinjamanResponse(
                saved.getPinjamanId(),
                saved.getJenisPinjaman(),
                saved.getDeskripsiPinjaman(),
                saved.getBunga(),
                saved.getBiayaLainnya(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updatePinjaman(Integer id, String jenisPinjaman, String deskripsiPinjaman, Double bunga, Double biayaLainnya) {
        Optional<pinjamanEntity> pinjamanOpt = pinjamanRepository.findById(id);

        if (pinjamanOpt.isEmpty()) {
            throw new EntityNotFoundException("pinjaman id tidak ditemukan");
        }

        pinjamanEntity pinjamanUpdate = pinjamanOpt.get();
        pinjamanUpdate.setJenisPinjaman(jenisPinjaman);
        pinjamanUpdate.setDeskripsiPinjaman(deskripsiPinjaman);
        pinjamanUpdate.setBunga(bunga);
        pinjamanUpdate.setBiayaLainnya(biayaLainnya);
        pinjamanUpdate.setUpdatedAt(LocalDate.now());
        pinjamanRepository.save(pinjamanUpdate);
    }

    @Override
    public String deletePinjaman(Integer id) {
        pinjamanEntity pinjamanDelete = pinjamanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("pinjaman id: " + id + " tidak ditemukan"));

        pinjamanRepository.delete(pinjamanDelete);

        return "pinjaman dengan ID: " + id + " Telah di hapus";
    }
}
