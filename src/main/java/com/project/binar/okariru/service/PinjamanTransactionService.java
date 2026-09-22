package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface PinjamanTransactionService {

//    List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> getAllPinjamanTransaction();


    Page<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> findAll(String status, String keyword, int page, int size);

    List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> findByCustomerId(Integer customerId, String status, String keyword);


    PinjamanTransactionServiceResponse.getPinjamanTransactionResponse getPinjamanTransactionById(Integer id);

    PinjamanTransactionServiceResponse.getPinjamanTransactionResponse addPinjamanTransaction(PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest addRequest);

    void updatePinjamanTransaction(Integer id, Integer customerId, Integer pinjamanId, Integer nominalPinjaman, Integer tenor,
                                    String statusPengajuan, LocalDate tanggalReview, LocalDate tanggalApproval,
                                   String noteMarketing, String noteBm, String noteBackOffice, Integer lastUpdateBy);

    // tahap review oleh MARKETING: Pengajuan -> Direview
    void reviewPinjamanTransaction(Integer id, String note);

    // tahap approval oleh BRANCH_MANAGER: Direview -> Disetujui/Ditolak
    void approvalPinjamanTransaction(Integer id, boolean approved, String note);

    // tahap pencairan oleh BACKOFFICE: Disetujui -> Dicairkan
    void disbursePinjamanTransaction(Integer id, String note);

    String deletePinjamanTransaction(Integer id);
}
