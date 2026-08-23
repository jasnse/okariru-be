package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.entity.MenugroupEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.MenuRepository;
import com.project.binar.okariru.repository.MenugroupRepository;
import com.project.binar.okariru.service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenugroupRepository menugroupRepository;
    private final EmployeRepository employeRepository;


    @Override
    public Page<MenuResponse.getMenuResponse> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("menuId").ascending());

        Page<MenuEntity> menuPage = menuRepository.searchMenu(keyword, pageable);

        return menuPage.map(menu -> new MenuResponse.getMenuResponse(
                menu.getMenuId(),
                menu.getNamaMenu(),
                menu.getDeskripsiMenu(),
                menu.getPath(),
                menu.getIcon(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        ));
    }

//    @Override
//    public List<MenuResponse.getMenuResponse> getAllmenuService() {
//        return menuRepository.findAll()
//                .stream()
//                .map(menuList -> new MenuResponse.getMenuResponse(
//                        menuList.getMenuId(),
//                        menuList.getNamaMenu(),
//                        menuList.getDeskripsiMenu(),
//                        menuList.getPath(),
//                        menuList.getIcon(),
//                        menuList.getCreatedAt(),
//                        menuList.getUpdatedAt()
//                ))
//                .toList();
//    }

    @Override
    public List<MenuResponse.myMenuResponse> getMyMenu(String username) {
        EmployeEntity employee = employeRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new EntityNotFoundException("Employee tidak ditemukan"));

        List<Integer> roleGroupIds = employee.getRoleGroups().stream()
                .map(RolegroupEntity::getRoleGroupId)
                .toList();

        if (roleGroupIds.isEmpty()) {
            return List.of();
        }

        List<MenugroupEntity> menuGroups = menugroupRepository.findByRole_RoleGroupIdIn(roleGroupIds);

        return menuGroups.stream()
                .map(MenugroupEntity::getMenu)
                .distinct()
                .map(menu -> new MenuResponse.myMenuResponse(
                        menu.getMenuId(),
                        menu.getNamaMenu(),
                        menu.getPath(),
                        menu.getIcon()
                ))
                .toList();
    }

//    @Override
//    public MenuResponse.getMenuResponse getmenuById(Integer idmenu) {
//        MenuEntity menu = menuRepository.findById(idmenu)
//                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idmenu + " tidak ditemukan"));
//        return new MenuResponse.getMenuResponse(
//                menu.getMenuId(),
//                menu.getNamaMenu(),
//                menu.getDeskripsiMenu(),
//                menu.getPath(),
//                menu.getIcon(),
//                menu.getCreatedAt(),
//                menu.getUpdatedAt()
//        );
//    }

    @Override
    public MenuResponse.getMenuResponse addMenu(MenuRequest.menuAddRequest addRequest){
        if (menuRepository.existsByNamaMenu(addRequest.namaMenu)) {
            throw new IllegalArgumentException("Menu dengan nama " + addRequest.namaMenu + " sudah ada");
        }

        MenuEntity menu = new MenuEntity();
        menu.setNamaMenu(addRequest.namaMenu);
        menu.setDeskripsiMenu(addRequest.deskripsiMenu);
        menu.setPath(addRequest.path);
        menu.setIcon(addRequest.icon);
        menu.setCreatedAt(LocalDate.now());

        MenuEntity saved = menuRepository.save(menu);
        return new MenuResponse.getMenuResponse(
                saved.getMenuId(),
                saved.getNamaMenu(),
                saved.getDeskripsiMenu(),
                saved.getPath(),
                saved.getIcon(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updatemenu(Integer id, String namamenu, String deskripsimenu, String path, String icon){
        Optional<MenuEntity> menuOpt = menuRepository.findById(id);

        if (menuOpt.isEmpty()) {
            throw new EntityNotFoundException("menu id tidak ditemukan");
        }

        if (menuRepository.existsByNamaMenuAndMenuIdNot(namamenu, id)) {
            throw new IllegalArgumentException("Menu dengan nama " + namamenu + " sudah ada");
        }

        MenuEntity menuUpdate = menuOpt.get();

        menuUpdate.setNamaMenu(namamenu);
        menuUpdate.setDeskripsiMenu(deskripsimenu);
        menuUpdate.setPath(path);
        menuUpdate.setIcon(icon);
        menuUpdate.setUpdatedAt(LocalDate.now());

        menuRepository.save(menuUpdate);
    }

    @Override
    public String deleteMenu(Integer id) {
        MenuEntity menuDelete = menuRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("menu id: " + id + " " + "tidak ditemukan" ));

        menuRepository.delete(menuDelete);

        return "menu dengan ID: " + id + " " + "Telah di hapus";
    }
}
