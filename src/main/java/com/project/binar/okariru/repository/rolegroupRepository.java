package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.rolegroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface rolegroupRepository extends JpaRepository<rolegroupEntity, Integer> {
    Optional<rolegroupEntity> findById(Integer id);
}
