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
        String namaGroupRole;
        LocalDate createdAt;
        LocalDate updatedAt;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class roleGroupMemberResponse {
        Integer employeeId;
        String userName;
        String email;
        String nip;
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
