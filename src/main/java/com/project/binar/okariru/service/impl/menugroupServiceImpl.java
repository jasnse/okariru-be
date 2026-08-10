package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.menugroupRequest;
import com.project.binar.okariru.dto.menugroupResponse;
import com.project.binar.okariru.entity.menuEntity;
import com.project.binar.okariru.entity.menugroupEntity;
import com.project.binar.okariru.entity.rolegroupEntity;
import com.project.binar.okariru.repository.menuRepository;
import com.project.binar.okariru.repository.menugroupRepository;
import com.project.binar.okariru.repository.rolegroupRepository;
import com.project.binar.okariru.service.menugroupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class menugroupServiceImpl implements menugroupService {

    private final menugroupRepository menugroupRepository;
    private final menuRepository menuRepository;
    private final rolegroupRepository rolegroupRepository;

    @Override
    public List<menugroupResponse.getMenuGroupResponse> getAllMenuGroup() {
        return menugroupRepository.findAll()
                .stream()
                .map(mg -> new menugroupResponse.getMenuGroupResponse(
                        mg.getMenuGroupId(),
                        mg.getMenu().getMenuId(),
                        mg.getRole().getRoleGroupId(),
                        mg.getNamaGroupMenu(),
                        mg.getCreatedAt(),
                        mg.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public menugroupResponse.getMenuGroupResponse getMenuGroupById(Integer id) {
        menugroupEntity mg = menugroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("menu group dengan Id " + id + " tidak ditemukan"));
        return new menugroupResponse.getMenuGroupResponse(
                mg.getMenuGroupId(),
                mg.getMenu().getMenuId(),
                mg.getRole().getRoleGroupId(),
                mg.getNamaGroupMenu(),
                mg.getCreatedAt(),
                mg.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public menugroupResponse.getMenuGroupResponse addMenuGroup(menugroupRequest.menuGroupAddRequest addRequest) {
        menuEntity menu = menuRepository.findById(addRequest.menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu dengan id " + addRequest.menuId + " tidak ditemukan"));

        rolegroupEntity roleGroup = rolegroupRepository.findById(addRequest.roleGroupId)
                .orElseThrow(() -> new EntityNotFoundException("Role group dengan id " + addRequest.roleGroupId + " tidak ditemukan"));

        menugroupEntity menuGroup = new menugroupEntity();
        menuGroup.setMenu(menu);
        menuGroup.setRole(roleGroup);
        menuGroup.setNamaGroupMenu(addRequest.namaGroupMenu);
        menuGroup.setCreatedAt(LocalDate.now());

        menugroupEntity saved = menugroupRepository.save(menuGroup);

        return new menugroupResponse.getMenuGroupResponse(
                saved.getMenuGroupId(),
                saved.getMenu().getMenuId(),
                saved.getRole().getRoleGroupId(),
                saved.getNamaGroupMenu(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateMenuGroup(Integer id, Integer menuId, Integer roleGroupId, String namaGroupMenu) {
        Optional<menugroupEntity> mgOpt = menugroupRepository.findById(id);

        if (mgOpt.isEmpty()) {
            throw new EntityNotFoundException("menu group id tidak ditemukan");
        }

        menuEntity menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu dengan id " + menuId + " tidak ditemukan"));

        rolegroupEntity roleGroup = rolegroupRepository.findById(roleGroupId)
                .orElseThrow(() -> new EntityNotFoundException("Role group dengan id " + roleGroupId + " tidak ditemukan"));

        menugroupEntity mgUpdate = mgOpt.get();
        mgUpdate.setMenu(menu);
        mgUpdate.setRole(roleGroup);
        mgUpdate.setNamaGroupMenu(namaGroupMenu);
        mgUpdate.setUpdatedAt(LocalDate.now());
        menugroupRepository.save(mgUpdate);
    }

    @Override
    public String deleteMenuGroup(Integer id) {
        menugroupEntity mgDelete = menugroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("menu group id: " + id + " tidak ditemukan"));

        menugroupRepository.delete(mgDelete);

        return "menu group dengan ID: " + id + " Telah di hapus";
    }
}
