package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.employeRequest;
import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.entity.roleEntity;
import com.project.binar.okariru.repository.roleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class roleService {

    private final roleRepository roleRepository;

    //get ALl Role
    public List<roleResponse.getRoleResponse> getAllRoleService() {
        return roleRepository.findAll()
                .stream()
                .map(roleList -> new roleResponse.getRoleResponse(
                        roleList.getRoleId(),
                        roleList.getNamaRole(),
                        roleList.getCreatedAt()
                ))
                .toList();
    }

    //add employe
    public roleResponse.getRoleResponse addRole(roleRequest.roleAddRequest addRequest){
        roleEntity role = new roleEntity();
        role.setNamaRole(addRequest.namaRole);
        role.setCreatedAt(LocalDate.now());


        roleEntity saved = roleRepository.save(role);
        return new roleResponse.getRoleResponse(
                saved.getRoleId(),
                saved.getNamaRole(),
                saved.getCreatedAt()
        );
    }
}
