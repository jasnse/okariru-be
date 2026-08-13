package com.project.binar.okariru.repository;


import com.project.binar.okariru.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    Optional<MenuEntity> findById(Integer id);
}
