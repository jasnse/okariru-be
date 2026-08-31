package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PinjamanTransactionRepository extends JpaRepository<PinjamanTransactionEntity, Integer> {


    @Query("SELECT pt FROM PinjamanTransactionEntity pt WHERE " +
            "(:status IS NULL OR pt.statusPengajuan = :status) AND " +
            "(:keyword IS NULL OR LOWER(pt.kodeTransaksi) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<PinjamanTransactionEntity> searchPinjamanTrx(@Param("status") String status, @Param("keyword") String keyword, Pageable pageable);
}
