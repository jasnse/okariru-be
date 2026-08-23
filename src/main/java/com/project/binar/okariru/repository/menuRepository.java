package com.project.binar.okariru.repository;


import com.project.binar.okariru.entity.MenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    Optional<MenuEntity> findById(Integer id);

    boolean existsByNamaMenu(String namaMenu);
    boolean existsByNamaMenuAndMenuIdNot(String namaMenu, Integer menuId);

    @Query("SELECT m FROM MenuEntity m WHERE " +
            "(:keyword IS NULL OR LOWER(m.namaMenu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MenuEntity> searchMenu(@Param("keyword") String keyword, Pageable pageable);
}
