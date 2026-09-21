package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.RoleRequest;
import com.project.binar.okariru.dto.RoleResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

//    List<RoleResponse.getRoleResponse> getAllRoleService();

    Page<RoleResponse.getRoleResponse> findAll(String keyword, int page, int size);

    RoleResponse.getRoleResponse getRoleById(Integer idRole);

//    RoleResponse.getRoleResponse addRole(RoleRequest.roleAddRequest addRequest);
//
//    void updateRole(Integer id, String namaRole);
//
//    String deleteRole(Integer id);
}
