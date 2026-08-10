package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.entity.roleEntity;
import com.project.binar.okariru.repository.roleRepository;
import com.project.binar.okariru.service.roleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class roleServiceImpl implements roleService {

    private final roleRepository roleRepository;

    @Override
    public List<roleResponse.getRoleResponse> getAllRoleService() {
        return roleRepository.findAll()
                .stream()
                .map(roleList -> new roleResponse.getRoleResponse(
                        roleList.getRoleId(),
                        roleList.getNamaRole(),
                        roleList.getCreatedAt(),
                        roleList.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public roleResponse.getRoleResponse getRoleById(Integer idRole) {
        roleEntity role = roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idRole + " tidak ditemukan"));
        return new roleResponse.getRoleResponse(
                role.getRoleId(),
                role.getNamaRole(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    @Override
    public roleResponse.getRoleResponse addRole(roleRequest.roleAddRequest addRequest){
        roleEntity role = new roleEntity();
        role.setNamaRole(addRequest.namaRole);
        role.setCreatedAt(LocalDate.now());

        roleEntity saved = roleRepository.save(role);
        return new roleResponse.getRoleResponse(
                saved.getRoleId(),
                saved.getNamaRole(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateRole(Integer id, String namaRole){
        Optional<roleEntity> roleOpt = roleRepository.findById(id);

        if (roleOpt.isEmpty()) {
            throw new EntityNotFoundException("role id tidak ditemukan");
        }

        roleEntity roleUpdate = roleOpt.get();
        roleUpdate.setNamaRole(namaRole);
        roleUpdate.setUpdatedAt(LocalDate.now());
        roleRepository.save(roleUpdate);
    }

    @Override
    public String deleteRole(Integer id) {
        roleEntity roleDelete = roleRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("role id: " + id + " " + "tidak ditemukan" ));

        roleRepository.delete(roleDelete);

        return "role dengan ID: " + id + " " + "Telah di hapus";
    }
}
