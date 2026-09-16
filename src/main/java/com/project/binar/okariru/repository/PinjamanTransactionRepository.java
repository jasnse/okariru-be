package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.entity.PlafondEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PinjamanTransactionRepository extends JpaRepository<PinjamanTransactionEntity, Integer> {


    @Query("SELECT pt FROM PinjamanTransactionEntity pt WHERE " +
            "(:status IS NULL OR pt.statusPengajuan = :status) AND " +
            "(:keyword IS NULL OR LOWER(pt.kodeTransaksi) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<PinjamanTransactionEntity> searchPinjamanTrx(@Param("status") String status, @Param("keyword") String keyword, Pageable pageable);

   //untuk cek plafond
    @Query("SELECT COALESCE(SUM(pt.nominalPinjaman), 0) FROM PinjamanTransactionEntity pt WHERE " +
            "pt.customer.customerId = :customerId AND pt.statusPengajuan IN ('Disetujui', 'Dicairkan')")
    long sumNominalPinjamanDisetujuiByCustomer(@Param("customerId") Integer customerId);

//    @Query("SELECT pt FROM PinjamanTransactionEntity pt WHERE " +
//            "pt.customer.customerId = :customerId AND " + "(:status IS NULL OR pt.statusPengajuan = :status) AND " +
//            "(:keyword IS NULL OR LOWER(pt.kodeTransaksi) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    List<PinjamanTransactionEntity> findByCustomer(@Param("customerId") Integer customerId, @Param("status") String status, @Param("keyword") String keyword);

    @Query("SELECT pt FROM PinjamanTransactionEntity pt WHERE " +
            "pt.customer.customerId = :customerId AND " +
            "(:status IS NULL OR pt.statusPengajuan = :status) AND " +
            "(:keyword IS NULL OR LOWER(pt.kodeTransaksi) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    List<PinjamanTransactionEntity> findByCustomer(@Param("customerId") Integer customerId, @Param("status") String status, @Param("keyword") String keyword);
}
