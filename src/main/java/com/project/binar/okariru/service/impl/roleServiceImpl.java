package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.RoleRequest;
import com.project.binar.okariru.dto.RoleResponse;
import com.project.binar.okariru.entity.RoleEntity;
import com.project.binar.okariru.repository.RoleRepository;
import com.project.binar.okariru.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponse.getRoleResponse> getAllRoleService() {
        return roleRepository.findAll()
                .stream()
                .map(roleList -> new RoleResponse.getRoleResponse(
                        roleList.getRoleId(),
                        roleList.getNamaRole(),
                        roleList.getCreatedAt(),
                        roleList.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public RoleResponse.getRoleResponse getRoleById(Integer idRole) {
        RoleEntity role = roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idRole + " tidak ditemukan"));
        return new RoleResponse.getRoleResponse(
                role.getRoleId(),
                role.getNamaRole(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    @Override
    public RoleResponse.getRoleResponse addRole(RoleRequest.roleAddRequest addRequest){
        RoleEntity role = new RoleEntity();
        role.setNamaRole(addRequest.namaRole);
        role.setCreatedAt(LocalDate.now());

        RoleEntity saved = roleRepository.save(role);
        return new RoleResponse.getRoleResponse(
                saved.getRoleId(),
                saved.getNamaRole(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateRole(Integer id, String namaRole){
        Optional<RoleEntity> roleOpt = roleRepository.findById(id);

        if (roleOpt.isEmpty()) {
            throw new EntityNotFoundException("role id tidak ditemukan");
        }

        RoleEntity roleUpdate = roleOpt.get();
        roleUpdate.setNamaRole(namaRole);
        roleUpdate.setUpdatedAt(LocalDate.now());
        roleRepository.save(roleUpdate);
    }

    @Override
    public String deleteRole(Integer id) {
        RoleEntity roleDelete = roleRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("role id: " + id + " " + "tidak ditemukan" ));

        roleRepository.delete(roleDelete);

        return "role dengan ID: " + id + " " + "Telah di hapus";
    }
}
