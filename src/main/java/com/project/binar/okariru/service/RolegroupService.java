package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.dto.RolegroupRequest;
import com.project.binar.okariru.dto.RolegroupResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RolegroupService {

    List<RolegroupResponse.getRoleGroupResponse> getAllRoleGroup(String keyword);

    RolegroupResponse.getRoleGroupResponse getRoleById(Integer idRG);

    RolegroupResponse.getRoleGroupResponse addRoleGroup(RolegroupRequest.roleGroupAddRequest addRequest);

    void updateRoleGroup(Integer id, Integer roleId, String namaGroupRole);

    String deleteRoleGroup(Integer id);

    Page<RolegroupResponse.roleGroupMemberResponse> getMembers(Integer roleGroupId, String keyword, int page, int size);

    List<EmployeResponse.employeGetResponse> findEmployeesNonRG();

    void assignEmployee(Integer roleGroupId, Integer employeeId);

    void removeEmployee(Integer roleGroupId, Integer employeeId);
}
