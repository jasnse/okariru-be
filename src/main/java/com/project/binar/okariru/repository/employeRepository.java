package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.employeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface employeRepository extends JpaRepository<employeEntity, Integer> {
    Optional<employeEntity> findByUserName(String name);
}
