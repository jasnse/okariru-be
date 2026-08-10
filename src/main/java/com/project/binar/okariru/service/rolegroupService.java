package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.rolegroupRequest;
import com.project.binar.okariru.dto.rolegroupResponse;

import java.util.List;

public interface rolegroupService {

    List<rolegroupResponse.getRoleGroupResponse> getAllRoleGroup();

    rolegroupResponse.getRoleGroupResponse getRoleById(Integer idRG);

    rolegroupResponse.getRoleGroupResponse addRoleGroup(rolegroupRequest.roleGroupAddRequest addRequest);

    void updateRoleGroup(Integer id, Integer roleId, Integer employeeId, String namaGroupRole);

    String deleteRoleGroup(Integer id);
}
