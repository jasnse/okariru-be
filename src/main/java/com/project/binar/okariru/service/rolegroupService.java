package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.RolegroupRequest;
import com.project.binar.okariru.dto.RolegroupResponse;

import java.util.List;

public interface RolegroupService {

    List<RolegroupResponse.getRoleGroupResponse> getAllRoleGroup();

    RolegroupResponse.getRoleGroupResponse getRoleById(Integer idRG);

    RolegroupResponse.getRoleGroupResponse addRoleGroup(RolegroupRequest.roleGroupAddRequest addRequest);

    void updateRoleGroup(Integer id, Integer roleId, Integer employeeId, String namaGroupRole);

    String deleteRoleGroup(Integer id);
}
