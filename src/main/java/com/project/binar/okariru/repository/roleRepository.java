package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>{

    Optional<RoleEntity> findById(Integer id);

    boolean existsByNamaRole(String namaRole);
    boolean existsByNamaRoleAndRoleIdNot(String namaRole, Integer roleId);
}
