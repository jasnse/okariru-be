package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolegroupRepository extends JpaRepository<RolegroupEntity, Integer> {
    Optional<RolegroupEntity> findById(Integer id);

    @Query("SELECT m FROM RolegroupEntity m WHERE " +
            "(:keyword IS NULL OR LOWER(m.namaGroupRole) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<RolegroupEntity> searchRoleGroup(@Param("keyword") String keyword);


}
