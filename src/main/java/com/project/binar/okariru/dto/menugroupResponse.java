package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class menugroupResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getMenuGroupResponse {
        Integer menuGroupId;
        Integer menuId;
        Integer roleGroupId;
        String namaGroupMenu;
        LocalDate createdAt;
        LocalDate updatedAt;
    }

    @Setter
    @Getter
    public static class menuGroupUpdateResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class menuGroupDeleteResponse {
        String Message;
    }
}
