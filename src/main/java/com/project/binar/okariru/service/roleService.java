package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.employeRequest;
import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.dto.roleRequest;
import com.project.binar.okariru.dto.roleResponse;
import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.entity.roleEntity;
import com.project.binar.okariru.repository.roleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    //get Role by Id
    public roleResponse.getRoleResponse getRoleById(Integer idRole) {
        roleEntity role = roleRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idRole + " tidak ditemukan"));
        return new roleResponse.getRoleResponse(
                role.getRoleId(),
                role.getNamaRole(),
                role.getCreatedAt()
        );
    }

    //add role
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

    //update Role
    @Transactional
    public void updateRole(Integer id, String namaRole){
        Optional<roleEntity> roleOpt = roleRepository.findById(id);

        if (roleOpt.isEmpty()) {
            throw new EntityNotFoundException("role id tidak ditemukan");
        }

        roleEntity roleUpdate = roleOpt.get();
        roleUpdate.setNamaRole(namaRole);
        roleUpdate.setCreatedAt(LocalDate.now());
        roleRepository.save(roleUpdate);
    }

    //delete role
    public String deleteRole(Integer id) {
        roleEntity roleDelete = roleRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("role id: " + id + " " + "tidak ditemukan" ));

        roleRepository.delete(roleDelete);

        return "role dengan ID: " + id + " " + "Telah di hapus";
    }

}
