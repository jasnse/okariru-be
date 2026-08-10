package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.menuRequest;
import com.project.binar.okariru.dto.menuResponse;
import com.project.binar.okariru.entity.menuEntity;
import com.project.binar.okariru.repository.menuRepository;
import com.project.binar.okariru.service.menuService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class menuServiceImpl implements menuService {

    private final menuRepository menuRepository;

    @Override
    public List<menuResponse.getMenuResponse> getAllmenuService() {
        return menuRepository.findAll()
                .stream()
                .map(menuList -> new menuResponse.getMenuResponse(
                        menuList.getMenuId(),
                        menuList.getNamaMenu(),
                        menuList.getDeskripsiMenu(),
                        menuList.getCreatedAt(),
                        menuList.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public menuResponse.getMenuResponse getmenuById(Integer idmenu) {
        menuEntity menu = menuRepository.findById(idmenu)
                .orElseThrow(() -> new EntityNotFoundException("role dengan Id " + idmenu + " tidak ditemukan"));
        return new menuResponse.getMenuResponse(
                menu.getMenuId(),
                menu.getNamaMenu(),
                menu.getDeskripsiMenu(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        );
    }

    @Override
    public menuResponse.getMenuResponse addMenu(menuRequest.menuAddRequest addRequest){
        menuEntity menu = new menuEntity();
        menu.setNamaMenu(addRequest.namaMenu);
        menu.setDeskripsiMenu(addRequest.deskripsiMenu);
        menu.setCreatedAt(LocalDate.now());

        menuEntity saved = menuRepository.save(menu);
        return new menuResponse.getMenuResponse(
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
        Optional<menuEntity> menuOpt = menuRepository.findById(id);

        if (menuOpt.isEmpty()) {
            throw new EntityNotFoundException("menu id tidak ditemukan");
        }

        menuEntity menuUpdate = menuOpt.get();

        menuUpdate.setNamaMenu(namamenu);
        menuUpdate.setDeskripsiMenu(deskripsimenu);
        menuUpdate.setUpdatedAt(LocalDate.now());

        menuRepository.save(menuUpdate);
    }

    @Override
    public String deleteMenu(Integer id) {
        menuEntity menuDelete = menuRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("menu id: " + id + " " + "tidak ditemukan" ));

        menuRepository.delete(menuDelete);

        return "menu dengan ID: " + id + " " + "Telah di hapus";
    }
}
