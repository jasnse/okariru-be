package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;

import java.time.LocalDate;
import java.util.List;

public interface PinjamanTransactionService {

    List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> getAllPinjamanTransaction();

    PinjamanTransactionServiceResponse.getPinjamanTransactionResponse getPinjamanTransactionById(Integer id);

    PinjamanTransactionServiceResponse.getPinjamanTransactionResponse addPinjamanTransaction(PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest addRequest);

    void updatePinjamanTransaction(Integer id, Integer customerId, Integer pinjamanId, Integer nominalPinjaman,
                                    String statusPengajuan, LocalDate tanggalReview, LocalDate tanggalApproval,
                                    String noteApproval, String rejectNote, Integer lastUpdateBy);

    String deletePinjamanTransaction(Integer id);
}
