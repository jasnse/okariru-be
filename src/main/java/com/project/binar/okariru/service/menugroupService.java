package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.menugroupRequest;
import com.project.binar.okariru.dto.menugroupResponse;

import java.util.List;

public interface menugroupService {

    List<menugroupResponse.getMenuGroupResponse> getAllMenuGroup();

    menugroupResponse.getMenuGroupResponse getMenuGroupById(Integer id);

    menugroupResponse.getMenuGroupResponse addMenuGroup(menugroupRequest.menuGroupAddRequest addRequest);

    void updateMenuGroup(Integer id, Integer menuId, Integer roleGroupId, String namaGroupMenu);

    String deleteMenuGroup(Integer id);
}
