package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.PinjamanRequest;
import com.project.binar.okariru.dto.PinjamanResponse;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.repository.PinjamanRepository;
import com.project.binar.okariru.service.PinjamanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PinjamanServiceImpl implements PinjamanService {

    private final PinjamanRepository pinjamanRepository;

    @Override
    @Cacheable(cacheNames = "pinjaman", key = "'all'")
    public List<PinjamanResponse.getPinjamanResponse> getAllPinjaman() {
        return pinjamanRepository.findAll()
                .stream()
                .map(pinjaman -> new PinjamanResponse.getPinjamanResponse(
                        pinjaman.getPinjamanId(),
                        pinjaman.getJenisPinjaman(),
                        pinjaman.getDeskripsiPinjaman(),
                        pinjaman.getBunga(),
                        pinjaman.getBiayaLainnya(),
                        pinjaman.getCreatedAt(),
                        pinjaman.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Page<PinjamanResponse.getPinjamanResponse> findAll(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("pinjamanId").ascending());

        Page<PinjamanEntity> pinjamanPage = pinjamanRepository.searchPinjaman(keyword, pageable);

        return pinjamanPage.map(pinjaman -> new PinjamanResponse.getPinjamanResponse(
                pinjaman.getPinjamanId(),
                pinjaman.getJenisPinjaman(),
                pinjaman.getDeskripsiPinjaman(),
                pinjaman.getBunga(),
                pinjaman.getBiayaLainnya(),
                pinjaman.getCreatedAt(),
                pinjaman.getUpdatedAt()
        ));
    }

    @Override
    @Cacheable(cacheNames = "pinjaman", key = "#id")
    public PinjamanResponse.getPinjamanResponse getPinjamanById(Integer id) {
        PinjamanEntity pinjaman = pinjamanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("pinjaman dengan Id " + id + " tidak ditemukan"));
        return new PinjamanResponse.getPinjamanResponse(
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
    @CacheEvict(cacheNames = "pinjaman", allEntries = true)
    public PinjamanResponse.getPinjamanResponse addPinjaman(PinjamanRequest.pinjamanAddRequest addRequest) {
        if (pinjamanRepository.existsByJenisPinjaman(addRequest.jenisPinjaman)) {
            throw new IllegalArgumentException("Jenis pinjaman " + addRequest.jenisPinjaman + " sudah ada");
        }

        PinjamanEntity pinjaman = new PinjamanEntity();
        pinjaman.setJenisPinjaman(addRequest.jenisPinjaman);
        pinjaman.setDeskripsiPinjaman(addRequest.deskripsiPinjaman);
        pinjaman.setBunga(addRequest.bunga);
        pinjaman.setBiayaLainnya(addRequest.biayaLainnya);
        pinjaman.setCreatedAt(LocalDate.now());

        PinjamanEntity saved = pinjamanRepository.save(pinjaman);
        return new PinjamanResponse.getPinjamanResponse(
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
    @CacheEvict(cacheNames = "pinjaman", allEntries = true)
    public void updatePinjaman(Integer id, String jenisPinjaman, String deskripsiPinjaman, Double bunga, Double biayaLainnya) {
        Optional<PinjamanEntity> pinjamanOpt = pinjamanRepository.findById(id);

        if (pinjamanOpt.isEmpty()) {
            throw new EntityNotFoundException("pinjaman id tidak ditemukan");
        }

        if (pinjamanRepository.existsByJenisPinjamanAndPinjamanIdNot(jenisPinjaman, id)) {
            throw new IllegalArgumentException("Jenis pinjaman " + jenisPinjaman + " sudah ada");
        }

        PinjamanEntity pinjamanUpdate = pinjamanOpt.get();
        pinjamanUpdate.setJenisPinjaman(jenisPinjaman);
        pinjamanUpdate.setDeskripsiPinjaman(deskripsiPinjaman);
        pinjamanUpdate.setBunga(bunga);
        pinjamanUpdate.setBiayaLainnya(biayaLainnya);
        pinjamanUpdate.setUpdatedAt(LocalDate.now());
        pinjamanRepository.save(pinjamanUpdate);
    }

    @Override
    @CacheEvict(cacheNames = "pinjaman", allEntries = true)
    public String deletePinjaman(Integer id) {
        PinjamanEntity pinjamanDelete = pinjamanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("pinjaman id: " + id + " tidak ditemukan"));

        pinjamanRepository.delete(pinjamanDelete);

        return "pinjaman dengan ID: " + id + " Telah di hapus";
    }
}
