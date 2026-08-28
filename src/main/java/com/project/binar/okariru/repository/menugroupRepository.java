package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.MenugroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenugroupRepository extends JpaRepository<MenugroupEntity, Integer> {

    boolean existsByMenu_MenuIdAndRole_RoleGroupId(Integer menuId, Integer roleGroupId);
    boolean existsByMenu_MenuIdAndRole_RoleGroupIdAndMenuGroupIdNot(Integer menuId, Integer roleGroupId, Integer menuGroupId);


    //    SELECT mg.* FROM core.menu_group mg
    //    JOIN core.role_group rg  ON mg.role_id = rg.role_group_id
    //    WHERE rg.role_group_id IN (6)
    //untuk get my menu (findByRole_RoleGroupIdIn)
    List<MenugroupEntity> findByRole_RoleGroupId(Integer roleGroupId);
}
