package com.project.binar.okariru.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class rolegroupRequest {

    public static class roleGroupAddRequest {
        @NotNull(message = "role_id harus diisi")
        public Integer roleId;

        @NotNull(message = "employee_id harus diisi")
        public Integer employeeId;

        @NotBlank(message = "nama group role gak boleh kosong")
        public String namaGroupRole;
    }

    public static class roleGroupUpdateRequest{
        @NotNull(message = "role id harus di pilih")
        public Integer roleId;
        @NotNull(message = "employee id harus di pilih")
        public Integer employeeId;
        @NotBlank(message = "nama Group Role harus di pilih")
        public String namaGroupRole;
    }
}
