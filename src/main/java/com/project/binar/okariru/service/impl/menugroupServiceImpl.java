package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.dto.MenugroupRequest;
import com.project.binar.okariru.dto.MenugroupResponse;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.MenugroupEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.MenuRepository;
import com.project.binar.okariru.repository.MenugroupRepository;
import com.project.binar.okariru.repository.RolegroupRepository;
import com.project.binar.okariru.service.MenugroupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenugroupServiceImpl implements MenugroupService {

    private final MenugroupRepository menugroupRepository;
    private final MenuRepository menuRepository;
    private final RolegroupRepository rolegroupRepository;

    @Override
    public List<MenugroupResponse.getMenuGroupResponse> getAllMenuGroup() {
        return menugroupRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MenugroupResponse.getMenuGroupResponse> getMenuGroupsByRoleGroup(Integer roleGroupId) {
        if (!rolegroupRepository.existsById(roleGroupId)) {
            throw new EntityNotFoundException("Role group dengan id " + roleGroupId + " tidak ditemukan");
        }

        return menugroupRepository.findByRole_RoleGroupId(roleGroupId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<MenuResponse.getMenuResponse> getMenusNotInRoleGroup(Integer roleGroupId) {
        if (!rolegroupRepository.existsById(roleGroupId)) {
            throw new EntityNotFoundException("Role group dengan id " + roleGroupId + " tidak ditemukan");
        }

        return menuRepository.findMenusNotInRoleGroup(roleGroupId)
                .stream()
                .map(menu -> new MenuResponse.getMenuResponse(
                        menu.getMenuId(),
                        menu.getNamaMenu(),
                        menu.getDeskripsiMenu(),
                        menu.getPath(),
                        menu.getIcon(),
                        menu.getCreatedAt(),
                        menu.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public MenugroupResponse.getMenuGroupResponse getMenuGroupById(Integer id) {
        MenugroupEntity mg = menugroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("menu group dengan Id " + id + " tidak ditemukan"));
        return toResponse(mg);
    }

    private MenugroupResponse.getMenuGroupResponse toResponse(MenugroupEntity mg) {
        return new MenugroupResponse.getMenuGroupResponse(
                mg.getMenuGroupId(),
                mg.getMenu().getMenuId(),
                mg.getMenu().getNamaMenu(),
                mg.getRole().getRoleGroupId(),
                mg.getNamaGroupMenu(),
                mg.getCreatedAt(),
                mg.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public MenugroupResponse.getMenuGroupResponse addMenuGroup(MenugroupRequest.menuGroupAddRequest addRequest) {
        MenuEntity menu = menuRepository.findById(addRequest.menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu dengan id " + addRequest.menuId + " tidak ditemukan"));

        RolegroupEntity roleGroup = rolegroupRepository.findById(addRequest.roleGroupId)
                .orElseThrow(() -> new EntityNotFoundException("Role group dengan id " + addRequest.roleGroupId + " tidak ditemukan"));

        if (menugroupRepository.existsByMenu_MenuIdAndRole_RoleGroupId(addRequest.menuId, addRequest.roleGroupId)) {
            throw new IllegalArgumentException("Kombinasi menu " + addRequest.menuId + " dan role group " + addRequest.roleGroupId + " sudah ada");
        }

        MenugroupEntity menuGroup = new MenugroupEntity();
        menuGroup.setMenu(menu);
        menuGroup.setRole(roleGroup);
        menuGroup.setNamaGroupMenu(addRequest.namaGroupMenu);
        menuGroup.setCreatedAt(LocalDate.now());

        MenugroupEntity saved = menugroupRepository.save(menuGroup);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void updateMenuGroup(Integer id, Integer menuId, Integer roleGroupId, String namaGroupMenu) {
        Optional<MenugroupEntity> mgOpt = menugroupRepository.findById(id);

        if (mgOpt.isEmpty()) {
            throw new EntityNotFoundException("menu group id tidak ditemukan");
        }

        MenuEntity menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("Menu dengan id " + menuId + " tidak ditemukan"));

        RolegroupEntity roleGroup = rolegroupRepository.findById(roleGroupId)
                .orElseThrow(() -> new EntityNotFoundException("Role group dengan id " + roleGroupId + " tidak ditemukan"));

        if (menugroupRepository.existsByMenu_MenuIdAndRole_RoleGroupIdAndMenuGroupIdNot(menuId, roleGroupId, id)) {
            throw new IllegalArgumentException("Kombinasi menu " + menuId + " dan role group " + roleGroupId + " sudah ada");
        }

        MenugroupEntity mgUpdate = mgOpt.get();
        mgUpdate.setMenu(menu);
        mgUpdate.setRole(roleGroup);
        mgUpdate.setNamaGroupMenu(namaGroupMenu);
        mgUpdate.setUpdatedAt(LocalDate.now());
        menugroupRepository.save(mgUpdate);
    }

    @Override
    public String deleteMenuGroup(Integer id) {
        MenugroupEntity mgDelete = menugroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("menu group id: " + id + " tidak ditemukan"));

        menugroupRepository.delete(mgDelete);

        return "menu group dengan ID: " + id + " Telah di hapus";
    }
}
