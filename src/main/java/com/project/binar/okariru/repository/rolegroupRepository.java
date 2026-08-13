package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.RolegroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolegroupRepository extends JpaRepository<RolegroupEntity, Integer> {
    Optional<RolegroupEntity> findById(Integer id);
}
