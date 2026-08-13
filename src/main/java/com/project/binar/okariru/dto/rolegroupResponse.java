package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class RolegroupResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getRoleGroupResponse{
        Integer roleGroupId;
        Integer roleId;
        Integer employeeId;
        String namaGroupRole;
        LocalDate createdAt;
        LocalDate updatedAt;
    }

    @Setter
    @Getter
    public static class roleGroupUpdateResponse{
        String Message;
    }


    @Setter
    @Getter
    public static class roleGroupDeleteResponse{
        String Message;
    }

}
