package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.AngsuranRequest;
import com.project.binar.okariru.dto.AngsuranResponse;

import java.time.LocalDate;
import java.util.List;

public interface AngsuranService {

    List<AngsuranResponse.getAngsuranResponse> getAllAngsuran();

    AngsuranResponse.getAngsuranResponse getAngsuranById(Integer id);

    AngsuranResponse.getAngsuranResponse addAngsuran(AngsuranRequest.angsuranAddRequest addRequest);

    List<AngsuranResponse.getAngsuranResponse> generateAngsuran(AngsuranRequest.angsuranGenerateRequest generateRequest);

    AngsuranResponse.angsuranBayarResponse bayarAngsuran(AngsuranRequest.angsuranBayarRequest bayarRequest);

    void updateAngsuran(Integer id, Integer transPinjamanId, Long jumlahPokok, Double jumlahBunga,
                         Integer totalAngsuran, LocalDate tanggalJatuhTempo, String statusAngsuran, Integer tenor);

    String deleteAngsuran(Integer id);
}
