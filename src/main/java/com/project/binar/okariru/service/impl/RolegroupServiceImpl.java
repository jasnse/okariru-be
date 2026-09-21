package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.EmployeResponse;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolegroupServiceImpl implements RolegroupService {

    private final RolegroupRepository rolegroupRepository;
    private final RoleRepository roleRepository;
    private final EmployeRepository employeRepository;

    @Override
    public List<RolegroupResponse.getRoleGroupResponse> getAllRoleGroup(String keyword) {
        // Panggil method searchRoleGroup yang sudah dibuat di Repository
        return rolegroupRepository.searchRoleGroup(keyword)
                .stream()
                .map(roleGList -> new RolegroupResponse.getRoleGroupResponse(
                        roleGList.getRoleGroupId(),
                        roleGList.getRole().getRoleId(),
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

        RolegroupEntity roleGroup = new RolegroupEntity();
        roleGroup.setRole(role);
        roleGroup.setNamaGroupRole(addRequest.namaGroupRole);
        roleGroup.setCreatedAt(LocalDate.now());

        RolegroupEntity saved = rolegroupRepository.save(roleGroup);

        return new RolegroupResponse.getRoleGroupResponse(
                saved.getRoleGroupId(),
                saved.getRole().getRoleId(),
                saved.getNamaGroupRole(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateRoleGroup(Integer id, Integer roleId, String namaGroupRole) {
        RolegroupEntity rgUpdate = rolegroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("role group id tidak ditemukan"));

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role dengan id " + roleId + " tidak ditemukan"));

        rgUpdate.setRole(role);
        rgUpdate.setNamaGroupRole(namaGroupRole);
        rgUpdate.setUpdatedAt(LocalDate.now());
        rolegroupRepository.save(rgUpdate);
    }

    @Override
    @Transactional
    public String deleteRoleGroup(Integer id) {
        RolegroupEntity roleGroupDelete = rolegroupRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("role id: " + id + " " + "tidak ditemukan" ));

        if (roleGroupDelete.getEmployees() != null && !roleGroupDelete.getEmployees().isEmpty()) {
            throw new IllegalArgumentException(
                    "Role group ini masih punya " + roleGroupDelete.getEmployees().size() + " member. Keluarkan semua member dulu sebelum dihapus."
            );
        }

        rolegroupRepository.delete(roleGroupDelete);

        return "roleGroup dengan ID: " + id + " " + "Telah di hapus";
    }

//    RBAC
    @Override
    public Page<RolegroupResponse.roleGroupMemberResponse> getMembers(Integer roleGroupId, String keyword, int page, int size) {
        if (!rolegroupRepository.existsById(roleGroupId)) {
            throw new EntityNotFoundException("role group id " + roleGroupId + " tidak ditemukan");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("employeeId").ascending());

        Page<EmployeEntity> employees = employeRepository.searchMembersByRoleGroup(roleGroupId, keyword, pageable);

        return employees.map(emp -> new RolegroupResponse.roleGroupMemberResponse(
                emp.getEmployeeId(),
                emp.getUserName(),
                emp.getEmail(),
                emp.getNip()
        ));
    }

    @Override
    public List<EmployeResponse.employeGetResponse> findEmployeesNonRG() {
        return employeRepository.findEmployeesWithoutRoleGroup()
                .stream()
                .map(listEmp -> new EmployeResponse.employeGetResponse(
                        listEmp.getEmployeeId(),
                        listEmp.getUserName(),
                        listEmp.getNip(),
                        listEmp.getEmail(),
                        listEmp.getJoinedDate(),
                        listEmp.getUpdatedAt()
                )).toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "menu:my", allEntries = true)
    public void assignEmployee(Integer roleGroupId, Integer employeeId) {
        RolegroupEntity roleGroup = rolegroupRepository.findById(roleGroupId)
                .orElseThrow(() -> new EntityNotFoundException("role group id " + roleGroupId + " tidak ditemukan"));

        EmployeEntity employee = employeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee dengan id " + employeeId + " tidak ditemukan"));

        //cek employe punya role group lain atau enggak
        if (employee.getRoleGroup() != null) {
            throw new IllegalArgumentException("Employee ini sudah tergabung di role group lain. Keluarkan dulu sebelum ditambahkan ke role group ini.");
        }

        //add employe ke rolegroup
        employee.setRoleGroup(roleGroup);
        employeRepository.save(employee);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "menu:my", allEntries = true)
    public void removeEmployee(Integer roleGroupId, Integer employeeId) {
        EmployeEntity employee = employeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee " + employeeId + " tidak ditemukan"));

        if (employee.getRoleGroup() == null || employee.getRoleGroup().getRoleGroupId() != roleGroupId) {
            throw new EntityNotFoundException("Employee " + employeeId + " tidak ada di role group " + roleGroupId);
        }

        employee.setRoleGroup(null);
        employeRepository.save(employee);
    }
}
