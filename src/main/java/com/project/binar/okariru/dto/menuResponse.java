package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class MenuResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getMenuResponse{
        Integer menuId;
        String namaMenu;
        String deskripsiMenu;
        String path;
        String icon;
        LocalDate created_at;
        LocalDate updated_at;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class myMenuResponse{
        Integer menuId;
        String namaMenu;
        String path;
        String icon;
    }

    @Setter
    @Getter
    public static class menuUpdateResponse{
        String message;
    }

    @Setter
    @Getter
    public static class menuDeleteResponse{
        String message;
        String status;
    }

}
