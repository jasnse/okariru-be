package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class EmployeResponse {

    public record employeGetResponse(
            Integer Id,
            String userName,
            String nip,
            String email,
            LocalDate joinedDate,
            LocalDate updatedDate
    ) {}

    @Setter
    @Getter
    public static class employeUpdateResponse{
        String Message;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class employeAddResponse{
        Integer Id;
        String userName;
        String nip;
        LocalDate joinedDate;
    }

    @Setter
    @Getter
    public static class employeDeleteResponse {
        String Message;
        String status;
    }

}
