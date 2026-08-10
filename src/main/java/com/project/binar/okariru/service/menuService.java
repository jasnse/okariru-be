package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.menuRequest;
import com.project.binar.okariru.dto.menuResponse;

import java.util.List;

public interface menuService {

    List<menuResponse.getMenuResponse> getAllmenuService();

    menuResponse.getMenuResponse getmenuById(Integer idmenu);

    menuResponse.getMenuResponse addMenu(menuRequest.menuAddRequest addRequest);

    void updatemenu(Integer id, String namamenu, String deskripsimenu);

    String deleteMenu(Integer id);
}
