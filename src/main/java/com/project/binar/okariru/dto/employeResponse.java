package com.project.binar.okariru.dto;

import java.time.LocalDate;

public class employeResponse {

    public record employeGetResponse(
            Integer Id,
            String userName,
            String nip,
            LocalDate joinedDate
    ) {}

}
