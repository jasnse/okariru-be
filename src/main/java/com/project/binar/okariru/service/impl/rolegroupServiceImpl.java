package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.rolegroupRequest;
import com.project.binar.okariru.dto.rolegroupResponse;
import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.entity.roleEntity;
import com.project.binar.okariru.entity.rolegroupEntity;
import com.project.binar.okariru.repository.employeRepository;
import com.project.binar.okariru.repository.roleRepository;
import com.project.binar.okariru.repository.rolegroupRepository;
import com.project.binar.okariru.service.rolegroupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class rolegroupServiceImpl implements rolegroupService {

    private final rolegroupRepository rolegroupRepository;
    private final roleRepository roleRepository;
    private final employeRepository employeRepository;

    @Override
    public List<rolegroupResponse.getRoleGroupResponse> getAllRoleGroup() {
        return rolegroupRepository.findAll()
                .stream()
                .map(roleGList -> new rolegroupResponse.getRoleGroupResponse(
                        roleGList.getRoleGroupId(),
                        roleGList.getRole().getRoleId(),
                        roleGList.getEmployee().getEmployeeId(),
                        roleGList.getNamaGroupRole(),
                        roleGList.getCreatedAt(),
                        roleGList.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public rolegroupResponse.getRoleGroupResponse getRoleById(Integer idRG) {
        rolegroupEntity role = rolegroupRepository.findById(idRG)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idRG + " tidak ditemukan"));
        return new rolegroupResponse.getRoleGroupResponse(
                role.getRoleGroupId(),
                role.getRole().getRoleId(),
                role.getEmployee().getEmployeeId(),
                role.getNamaGroupRole(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    @Override
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
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateRoleGroup(Integer id, Integer roleId, Integer employeeId, String namaGroupRole) {
        Optional<rolegroupEntity> rgOpt = rolegroupRepository.findById(id);

        if (rgOpt.isEmpty()) {
            throw new EntityNotFoundException("role group id tidak ditemukan");
        }

        rolegroupEntity rgUpdate = rgOpt.get();
        rgUpdate.getRole().setRoleId(roleId);
        rgUpdate.getEmployee().setEmployeeId(employeeId);
        rgUpdate.setNamaGroupRole(namaGroupRole);
        rgUpdate.setUpdatedAt(LocalDate.now());
        rolegroupRepository.save(rgUpdate);
    }

    @Override
    public String deleteRoleGroup(Integer id) {
        rolegroupEntity roleGroupDelete = rolegroupRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("role id: " + id + " " + "tidak ditemukan" ));

        rolegroupRepository.delete(roleGroupDelete);

        return "roleGroup dengan ID: " + id + " " + "Telah di hapus";
    }
}
