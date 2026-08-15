package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.EmployeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeRepository extends JpaRepository<EmployeEntity, Integer> {
    Optional<EmployeEntity> findByUserName(String name);
    Optional<EmployeEntity> findById(Integer id);

    @Query("SELECT e FROM EmployeEntity e WHERE " +
            "(:keyword IS NULL OR LOWER(e.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<EmployeEntity> searchEmployees(@Param("keyword") String keyword, Pageable pageable);

    @Query(" SELECT me FROM EmployeEntity me WHERE me.userName = :username")
    Optional<EmployeEntity> findByUsername(@Param("username") String username);

    @Query("SELECT e FROM EmployeEntity e " +
            "LEFT JOIN FETCH e.roleGroups rg " +
            "LEFT JOIN FETCH rg.role " +
            "WHERE e.userName = :username")
    Optional<EmployeEntity> findByUsernameWithRoles(@Param("username") String username);

    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByNip(String nip);
    boolean existsByEmailAndEmployeeIdNot(String email, Integer employeeId);
}
