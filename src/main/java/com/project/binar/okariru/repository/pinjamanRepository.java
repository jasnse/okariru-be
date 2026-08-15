package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.PinjamanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinjamanRepository extends JpaRepository<PinjamanEntity, Integer> {

    boolean existsByJenisPinjaman(String jenisPinjaman);
    boolean existsByJenisPinjamanAndPinjamanIdNot(String jenisPinjaman, Integer pinjamanId);
}
