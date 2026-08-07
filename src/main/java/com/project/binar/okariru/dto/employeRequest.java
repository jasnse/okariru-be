package com.project.binar.okariru.dto;

import java.time.LocalDate;

public class employeRequest {

    public static class employeAddRequest{
         public String username;
         public String email;
         public String password;
         public String nip;
         public LocalDate joinedDate;
    }

    public static class employeChangeCredentialRequest{
         public String email;
         public String password;
    }
}
