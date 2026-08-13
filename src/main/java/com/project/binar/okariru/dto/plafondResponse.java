package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class PlafondResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getPlafondResponse {
        Integer plafondId;
        Integer userId;
        Integer totalPlafond;
        String deskripsiPlafond;
        Integer createdBy;
        LocalDate createdAt;
        Integer updatedBy;
        LocalDate updatedAt;
    }

    @Setter
    @Getter
    public static class plafondUpdateResponse {
        String Message;
    }

    @Setter
    @Getter
    public static class plafondDeleteResponse {
        String Message;
    }
}
