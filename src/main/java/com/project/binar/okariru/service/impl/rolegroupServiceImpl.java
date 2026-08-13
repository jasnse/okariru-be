package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.RolegroupRequest;
import com.project.binar.okariru.dto.RolegroupResponse;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.RoleEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.RoleRepository;
import com.project.binar.okariru.repository.RolegroupRepository;
import com.project.binar.okariru.service.RolegroupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolegroupServiceImpl implements RolegroupService {

    private final RolegroupRepository rolegroupRepository;
    private final RoleRepository roleRepository;
    private final EmployeRepository employeRepository;

    @Override
    public List<RolegroupResponse.getRoleGroupResponse> getAllRoleGroup() {
        return rolegroupRepository.findAll()
                .stream()
                .map(roleGList -> new RolegroupResponse.getRoleGroupResponse(
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
    public RolegroupResponse.getRoleGroupResponse getRoleById(Integer idRG) {
        RolegroupEntity role = rolegroupRepository.findById(idRG)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idRG + " tidak ditemukan"));
        return new RolegroupResponse.getRoleGroupResponse(
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
    public RolegroupResponse.getRoleGroupResponse addRoleGroup(RolegroupRequest.roleGroupAddRequest addRequest){
        RoleEntity role = roleRepository.findById(addRequest.roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role dengan id " + addRequest.roleId + " tidak ditemukan"));

        EmployeEntity employee = employeRepository.findById(addRequest.employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee dengan id " + addRequest.employeeId + " tidak ditemukan"));

        RolegroupEntity roleGroup = new RolegroupEntity();
        roleGroup.setRole(role);
        roleGroup.setEmployee(employee);
        roleGroup.setNamaGroupRole(addRequest.namaGroupRole);
        roleGroup.setCreatedAt(LocalDate.now());

        RolegroupEntity saved = rolegroupRepository.save(roleGroup);

        return new RolegroupResponse.getRoleGroupResponse(
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
        Optional<RolegroupEntity> rgOpt = rolegroupRepository.findById(id);

        if (rgOpt.isEmpty()) {
            throw new EntityNotFoundException("role group id tidak ditemukan");
        }

        RolegroupEntity rgUpdate = rgOpt.get();
        rgUpdate.getRole().setRoleId(roleId);
        rgUpdate.getEmployee().setEmployeeId(employeeId);
        rgUpdate.setNamaGroupRole(namaGroupRole);
        rgUpdate.setUpdatedAt(LocalDate.now());
        rolegroupRepository.save(rgUpdate);
    }

    @Override
    public String deleteRoleGroup(Integer id) {
        RolegroupEntity roleGroupDelete = rolegroupRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("role id: " + id + " " + "tidak ditemukan" ));

        rolegroupRepository.delete(roleGroupDelete);

        return "roleGroup dengan ID: " + id + " " + "Telah di hapus";
    }
}
