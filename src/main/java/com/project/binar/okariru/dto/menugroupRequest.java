package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MenugroupRequest {

    public static class menuGroupAddRequest {
        @NotNull(message = "menu id harus di pilih")
        public Integer menuId;
        @NotNull(message = "role group id harus di pilih")
        public Integer roleGroupId;
        @NotBlank(message = "nama Group Menu harus di isi")
        public String namaGroupMenu;
    }

    public static class menuGroupUpdateRequest {
        @NotNull(message = "menu id harus di pilih")
        public Integer menuId;
        @NotNull(message = "role group id harus di pilih")
        public Integer roleGroupId;
        @NotBlank(message = "nama Group Menu harus di isi")
        public String namaGroupMenu;
    }
}
