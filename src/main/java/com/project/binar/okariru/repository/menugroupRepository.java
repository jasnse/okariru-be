package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.MenugroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenugroupRepository extends JpaRepository<MenugroupEntity, Integer> {

    boolean existsByMenu_MenuIdAndRole_RoleGroupId(Integer menuId, Integer roleGroupId);
    boolean existsByMenu_MenuIdAndRole_RoleGroupIdAndMenuGroupIdNot(Integer menuId, Integer roleGroupId, Integer menuGroupId);

    List<MenugroupEntity> findByRole_RoleGroupIdIn(List<Integer> roleGroupIds);
}
