package com.project.binar.okariru.repository;


import com.project.binar.okariru.entity.menuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface menuRepository extends JpaRepository<menuEntity, Integer> {

    Optional<menuEntity> findById(Integer id);
}
