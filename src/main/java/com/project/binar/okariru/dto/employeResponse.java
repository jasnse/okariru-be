package com.project.binar.okariru.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class employeResponse {

    public record employeGetResponse(
            Integer Id,
            String userName,
            String nip,
            LocalDate joinedDate
    ) {}

    @Setter
    @Getter
    public static class employeUpdateResponse{
        String Message;
    }

    @Setter
    @Getter
    public static class employeDeleteResponse {
        String Message;
        String status;
    }

}
