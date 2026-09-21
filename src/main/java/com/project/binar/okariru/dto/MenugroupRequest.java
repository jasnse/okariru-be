package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MenugroupRequest {

    public static class menuGroupAddRequest {
        @NotNull(message = "menu id harus di pilih")
        public Integer menuId;
        @NotNull(message = "role group id harus di pilih")
        public Integer roleGroupId;
    }

    public static class menuGroupUpdateRequest {
        @NotNull(message = "menu id harus di pilih")
        public Integer menuId;
        @NotNull(message = "role group id harus di pilih")
        public Integer roleGroupId;
    }
}
