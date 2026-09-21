package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.PinjamanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PinjamanRepository extends JpaRepository<PinjamanEntity, Integer> {

    boolean existsByJenisPinjaman(String jenisPinjaman);
    boolean existsByJenisPinjamanAndPinjamanIdNot(String jenisPinjaman, Integer pinjamanId);


    @Query("SELECT p FROM PinjamanEntity p WHERE " +
            "(:keyword IS NULL OR LOWER(p.jenisPinjaman) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<PinjamanEntity> searchPinjaman(@Param("keyword") String keyword, Pageable pageable);
}
