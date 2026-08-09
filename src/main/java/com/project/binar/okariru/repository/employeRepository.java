package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.employeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface employeRepository extends JpaRepository<employeEntity, Integer> {
    Optional<employeEntity> findByUserName(String name);
    Optional<employeEntity> findById(Integer id);

    @Query("SELECT e FROM employeEntity e WHERE " +
            "(:keyword IS NULL OR LOWER(e.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<employeEntity> searchEmployees(@Param("keyword") String keyword, Pageable pageable);
}
