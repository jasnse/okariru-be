package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.sun.source.tree.LabeledStatementTree;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenuService {

//    List<MenuResponse.getMenuResponse> getAllmenuService();

    Page<MenuResponse.getMenuResponse> findAll(String keyword, int page, int size);

    List<MenuResponse.myMenuResponse> getMyMenu(String username);

//    MenuResponse.getMenuResponse getmenuById(Integer idmenu);

    MenuResponse.getMenuResponse addMenu(MenuRequest.menuAddRequest addRequest);

    void updatemenu(Integer id, String namamenu, String deskripsimenu, String path, String icon);

    String deleteMenu(Integer id);
}
