package com.project.binar.okariru.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class DocumentResponse {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class getDocumentResponse {
        Integer dokumenId;
        Integer transPinjamanId;
        String namaFile;
        String pathfile;
        Integer uploadBy;
        LocalDate uploadDate;
        String statusVerification;
    }

    @Setter
    @Getter
    public static class documentDeleteResponse {
        String Message;
    }
}
