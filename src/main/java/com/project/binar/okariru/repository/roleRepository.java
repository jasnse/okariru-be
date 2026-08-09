package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.entity.roleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface roleRepository extends JpaRepository<roleEntity, Integer>{

    Optional<roleEntity> findById(Integer id);
}
