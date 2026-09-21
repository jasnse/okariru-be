package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotNull;

public class PlafondRequest {

    public static class plafondAddRequest {
        @NotNull(message = "user id harus di pilih")
        public Integer userId;
        @NotNull(message = "total plafond harus diisi")
        public Integer totalPlafond;
        public String deskripsiPlafond;
        public Integer createdBy;
    }

    public static class plafondUpdateRequest {
        @NotNull(message = "user id harus di pilih")
        public Integer userId;
        @NotNull(message = "total plafond harus diisi")
        public Integer totalPlafond;
        public String deskripsiPlafond;
        public Integer updatedBy;
    }
}
