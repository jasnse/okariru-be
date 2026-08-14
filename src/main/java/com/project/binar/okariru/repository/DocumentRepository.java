package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {
    List<DocumentEntity> findByUploadBy_CustomerIdAndTransPinjaman_TransPinjamanId(Integer customerId, Integer transPinjamanId);
}
