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
        LocalDate created_at;
        LocalDate updated_at;
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
