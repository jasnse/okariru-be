package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RoleResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getRoleResponse{
        Integer role_id;
        String nama_role;
        LocalDate created_at;
        LocalDate updated_at;
    }

    @Setter
    @Getter
    public static class roleDeleteResponse {
        String Message;
        String status;
    }

    @Setter
    @Getter
    public static class roleUpdateResponse{
        String Message;
    }
}
