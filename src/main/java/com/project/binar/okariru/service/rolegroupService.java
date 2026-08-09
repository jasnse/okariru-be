package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.dto.rolegroupRequest;
import com.project.binar.okariru.dto.rolegroupResponse;
import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.entity.roleEntity;
import com.project.binar.okariru.entity.rolegroupEntity;
import com.project.binar.okariru.repository.employeRepository;
import com.project.binar.okariru.repository.roleRepository;
import com.project.binar.okariru.repository.rolegroupRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class rolegroupService {

    private final rolegroupRepository rolegroupRepository;
    private final roleRepository roleRepository;
    private final employeRepository employeRepository;

    //get all role Group
    public List<rolegroupResponse.getRoleGroupResponse> getAllRoleGroup() {
        return rolegroupRepository.findAll()
                .stream()
                .map(roleGList -> new rolegroupResponse.getRoleGroupResponse(
                        roleGList.getRoleGroupId(),
                        roleGList.getRole().getRoleId(),
                        roleGList.getEmployee().getEmployeeId(),
                        roleGList.getNamaGroupRole(),
                        roleGList.getCreatedAt()
                ))
                .toList();
    }

    //get Role by Id
    public rolegroupResponse.getRoleGroupResponse getRoleById(Integer idRG) {
        rolegroupEntity role = rolegroupRepository.findById(idRG)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idRG + " tidak ditemukan"));
        return new rolegroupResponse.getRoleGroupResponse(
                role.getRoleGroupId(),
                role.getRole().getRoleId(),
                role.getEmployee().getEmployeeId(),
                role.getNamaGroupRole(),
                role.getCreatedAt()
        );
    }

    //add role
    @Transactional
    public rolegroupResponse.getRoleGroupResponse addRoleGroup(rolegroupRequest.roleGroupAddRequest addRequest){
        roleEntity role = roleRepository.findById(addRequest.roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role dengan id " + addRequest.roleId + " tidak ditemukan"));

        employeEntity employee = employeRepository.findById(addRequest.employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee dengan id " + addRequest.employeeId + " tidak ditemukan"));


        rolegroupEntity roleGroup = new rolegroupEntity();
        roleGroup.setRole(role);
        roleGroup.setEmployee(employee);
        roleGroup.setNamaGroupRole(addRequest.namaGroupRole);
        roleGroup.setCreatedAt(LocalDate.now());

        rolegroupEntity saved = rolegroupRepository.save(roleGroup);

        return new rolegroupResponse.getRoleGroupResponse(
                saved.getRoleGroupId(),
                saved.getRole().getRoleId(),
                saved.getEmployee().getEmployeeId(),
                saved.getNamaGroupRole(),
                saved.getCreatedAt()
        );
    }



}
