package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.RolegroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolegroupRepository extends JpaRepository<RolegroupEntity, Integer> {
    Optional<RolegroupEntity> findById(Integer id);

    boolean existsByRole_RoleIdAndEmployee_EmployeeId(Integer roleId, Integer employeeId);
    boolean existsByRole_RoleIdAndEmployee_EmployeeIdAndRoleGroupIdNot(Integer roleId, Integer employeeId, Integer roleGroupId);
}
