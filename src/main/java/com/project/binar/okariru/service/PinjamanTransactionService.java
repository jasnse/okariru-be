package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface PinjamanTransactionService {

//    List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> getAllPinjamanTransaction();


    Page<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> findAll(String status, String keyword, int page, int size);


    PinjamanTransactionServiceResponse.getPinjamanTransactionResponse getPinjamanTransactionById(Integer id);

    PinjamanTransactionServiceResponse.getPinjamanTransactionResponse addPinjamanTransaction(PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest addRequest);

    void updatePinjamanTransaction(Integer id, Integer customerId, Integer pinjamanId, Integer nominalPinjaman, Integer tenor,
                                    String statusPengajuan, LocalDate tanggalReview, LocalDate tanggalApproval,
                                   String noteMarketing, String noteBm, String noteBackOffice, Integer lastUpdateBy);

    String deletePinjamanTransaction(Integer id);
}
