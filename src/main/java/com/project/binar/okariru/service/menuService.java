package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;

import java.util.List;

public interface MenuService {

    List<MenuResponse.getMenuResponse> getAllmenuService();

    MenuResponse.getMenuResponse getmenuById(Integer idmenu);

    MenuResponse.getMenuResponse addMenu(MenuRequest.menuAddRequest addRequest);

    void updatemenu(Integer id, String namamenu, String deskripsimenu);

    String deleteMenu(Integer id);
}
