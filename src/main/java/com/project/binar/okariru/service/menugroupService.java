package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;

import java.util.List;

public interface MenugroupService {

    List<MenugroupResponse.getMenuGroupResponse> getAllMenuGroup();

    MenugroupResponse.getMenuGroupResponse getMenuGroupById(Integer id);

    MenugroupResponse.getMenuGroupResponse addMenuGroup(MenugroupRequest.menuGroupAddRequest addRequest);

    void updateMenuGroup(Integer id, Integer menuId, Integer roleGroupId, String namaGroupMenu);

    String deleteMenuGroup(Integer id);
}
