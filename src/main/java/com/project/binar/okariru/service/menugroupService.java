package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenugroupService {

    List<MenugroupResponse.getMenuGroupResponse> getAllMenuGroup();

    Page<MenugroupResponse.getMenuGroupResponse> getMenuGroupsByRoleGroup(Integer roleGroupId, String keyword, int page, int size);

    List<MenuResponse.getMenuResponse> getMenusNotInRoleGroup(Integer roleGroupId);

    MenugroupResponse.getMenuGroupResponse getMenuGroupById(Integer id);

    MenugroupResponse.getMenuGroupResponse addMenuGroup(MenugroupRequest.menuGroupAddRequest addRequest);

    void updateMenuGroup(Integer id, Integer menuId, Integer roleGroupId);

    String deleteMenuGroup(Integer id);
}
