package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.MenuRequest;
import com.project.binar.okariru.dto.MenuResponse;
import com.project.binar.okariru.entity.MenuEntity;
import com.project.binar.okariru.repository.MenuRepository;
import com.project.binar.okariru.service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    @Override
    public List<MenuResponse.getMenuResponse> getAllmenuService() {
        return menuRepository.findAll()
                .stream()
                .map(menuList -> new MenuResponse.getMenuResponse(
                        menuList.getMenuId(),
                        menuList.getNamaMenu(),
                        menuList.getDeskripsiMenu(),
                        menuList.getCreatedAt(),
                        menuList.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public MenuResponse.getMenuResponse getmenuById(Integer idmenu) {
        MenuEntity menu = menuRepository.findById(idmenu)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idmenu + " tidak ditemukan"));
        return new MenuResponse.getMenuResponse(
                menu.getMenuId(),
                menu.getNamaMenu(),
                menu.getDeskripsiMenu(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        );
    }

    @Override
    public MenuResponse.getMenuResponse addMenu(MenuRequest.menuAddRequest addRequest){
        MenuEntity menu = new MenuEntity();
        menu.setNamaMenu(addRequest.namaMenu);
        menu.setDeskripsiMenu(addRequest.deskripsiMenu);
        menu.setCreatedAt(LocalDate.now());

        MenuEntity saved = menuRepository.save(menu);
        return new MenuResponse.getMenuResponse(
                saved.getMenuId(),
                saved.getNamaMenu(),
                saved.getDeskripsiMenu(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updatemenu(Integer id, String namamenu, String deskripsimenu){
        Optional<MenuEntity> menuOpt = menuRepository.findById(id);

        if (menuOpt.isEmpty()) {
            throw new EntityNotFoundException("menu id tidak ditemukan");
        }

        MenuEntity menuUpdate = menuOpt.get();

        menuUpdate.setNamaMenu(namamenu);
        menuUpdate.setDeskripsiMenu(deskripsimenu);
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
