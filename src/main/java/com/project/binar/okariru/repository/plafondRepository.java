package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.PlafondEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlafondRepository extends JpaRepository<PlafondEntity, Integer> {

    Optional<PlafondEntity> findByUser_CustomerId(Integer customerId);
}
