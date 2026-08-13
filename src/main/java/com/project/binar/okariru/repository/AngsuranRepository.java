package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.AngsuranEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AngsuranRepository extends JpaRepository<AngsuranEntity,Integer> {
    List<AngsuranEntity> findByTransPinjamanOrderByTenorAsc(PinjamanTransactionEntity transPinjaman);
}
