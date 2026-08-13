package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.RoleRequest;
import com.project.binar.okariru.dto.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse.getRoleResponse> getAllRoleService();

    RoleResponse.getRoleResponse getRoleById(Integer idRole);

    RoleResponse.getRoleResponse addRole(RoleRequest.roleAddRequest addRequest);

    void updateRole(Integer id, String namaRole);

    String deleteRole(Integer id);
}
