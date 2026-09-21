package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>{

    Optional<RoleEntity> findById(Integer id);

//    boolean existsByNamaRole(String namaRole);
//    boolean existsByNamaRoleAndRoleIdNot(String namaRole, Integer roleId);

    @Query("SELECT m FROM RoleEntity m WHERE " +
            "(:keyword IS NULL OR LOWER(m.namaRole) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<RoleEntity> searchRole(@Param("keyword") String keyword, Pageable pageable);
}
