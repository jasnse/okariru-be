package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;

import java.util.List;

public interface roleService {

    List<roleResponse.getRoleResponse> getAllRoleService();

    roleResponse.getRoleResponse getRoleById(Integer idRole);

    roleResponse.getRoleResponse addRole(roleRequest.roleAddRequest addRequest);

    void updateRole(Integer id, String namaRole);

    String deleteRole(Integer id);
}
