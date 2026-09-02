package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.PinjamanResponse;
import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import com.project.binar.okariru.entity.AppUser;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.entity.PlafondEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.PinjamanRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import com.project.binar.okariru.repository.PlafondRepository;
import com.project.binar.okariru.service.PinjamanTransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PinjamanTransactionServiceImpl implements PinjamanTransactionService {

    private final PinjamanTransactionRepository pinjamanTransactionRepository;
    private final CustomerRepository customerRepository;
    private final PinjamanRepository pinjamanRepository;
    private final EmployeRepository employeRepository;
    private final PlafondRepository plafondRepository;

//    @Override
//    public List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> getAllPinjamanTransaction() {
//        return pinjamanTransactionRepository.findAll()
//                .stream()
//                .map(trx -> new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(
//                        trx.getTransPinjamanId(),
//                        trx.getKodeTransaksi(),
//                        trx.getCustomer().getCustomerId(),
//                        trx.getPinjaman() != null ? trx.getPinjaman().getPinjamanId() : null,
//                        trx.getTanggalPengajuan(),
//                        trx.getTanggalReview(),
//                        trx.getTanggalApproval(),
//                        trx.getNominalPinjaman(),
//                        trx.getStatusPengajuan(),
//                        trx.getNoteApproval(),
//                        trx.getRejectNote(),
//                        trx.getLastUpdate(),
//                        trx.getLastUpdateBy() != null ? trx.getLastUpdateBy().getEmployeeId() : null
//                ))
//                .toList();
//    }

    @Override
    public Page<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> findAll(String status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transPinjamanId").ascending());

        Page<PinjamanTransactionEntity> pinjamanTransPage = pinjamanTransactionRepository.searchPinjamanTrx(status, keyword, pageable);

        return pinjamanTransPage.map(pinjamanTrx -> new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(
                pinjamanTrx.getTransPinjamanId(),
                pinjamanTrx.getKodeTransaksi(),
                pinjamanTrx.getCustomer().getCustomerId(),
                pinjamanTrx.getCustomer().getUserName(),
                pinjamanTrx.getPinjaman() != null ? pinjamanTrx.getPinjaman().getPinjamanId() : null,
                pinjamanTrx.getTanggalPengajuan(),
                pinjamanTrx.getTanggalReview(),
                pinjamanTrx.getTanggalApproval(),
                pinjamanTrx.getNominalPinjaman(),
                pinjamanTrx.getTenor(),
                pinjamanTrx.getStatusPengajuan(),
                pinjamanTrx.getNoteMarketing(),
                pinjamanTrx.getNoteBm(),
                pinjamanTrx.getNoteBackOffice(),
                pinjamanTrx.getLastUpdate(),
                pinjamanTrx.getLastUpdateBy() != null ? pinjamanTrx.getLastUpdateBy().getEmployeeId(): null

        ));

    }

    @Override
    public PinjamanTransactionServiceResponse.getPinjamanTransactionResponse getPinjamanTransactionById(Integer id) {
        PinjamanTransactionEntity trx = pinjamanTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("pinjaman transaction dengan Id " + id + " tidak ditemukan"));
        return new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(
                trx.getTransPinjamanId(),
                trx.getKodeTransaksi(),
                trx.getCustomer().getCustomerId(),
                trx.getCustomer().getUserName(),
                trx.getPinjaman() != null ? trx.getPinjaman().getPinjamanId() : null,
                trx.getTanggalPengajuan(),
                trx.getTanggalReview(),
                trx.getTanggalApproval(),
                trx.getNominalPinjaman(),
                trx.getTenor(),
                trx.getStatusPengajuan(),
                trx.getNoteMarketing(),
                trx.getNoteBm(),
                trx.getNoteBackOffice(),
                trx.getLastUpdate(),
                trx.getLastUpdateBy() != null ? trx.getLastUpdateBy().getEmployeeId() : null
        );
    }

    @Override
    @Transactional
    public PinjamanTransactionServiceResponse.getPinjamanTransactionResponse addPinjamanTransaction(PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest addRequest) {
        CustomerEntity customer = customerRepository.findById(addRequest.customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + addRequest.customerId + " tidak ditemukan"));
        
        Optional<PlafondEntity> plafondOpt = plafondRepository.findByUser_CustomerId(addRequest.customerId);

        if (plafondOpt.isPresent()) {
            PlafondEntity plafond = plafondOpt.get();
            long totalPlafond = plafond.getTotalPlafond() != null ? plafond.getTotalPlafond() : 0;
            long totalPinjamanDisetujui = pinjamanTransactionRepository
                    .sumNominalPinjamanDisetujuiByCustomer(addRequest.customerId);
            long sisaPlafond = totalPlafond - totalPinjamanDisetujui;

            if (addRequest.nominalPinjaman > sisaPlafond) {
                throw new IllegalArgumentException(
                        "Nominal pinjaman melebihi sisa plafond Anda (sisa plafond: " + sisaPlafond + ")");
            }
        }

        PinjamanTransactionEntity trx = new PinjamanTransactionEntity();
        trx.setCustomer(customer);
        trx.setNominalPinjaman(addRequest.nominalPinjaman);
        trx.setTenor(addRequest.tenor);
        trx.setTanggalPengajuan(LocalDate.now());
        trx.setStatusPengajuan("Pengajuan");

        if (addRequest.pinjamanId != null) {
            PinjamanEntity pinjaman = pinjamanRepository.findById(addRequest.pinjamanId)
                    .orElseThrow(() -> new EntityNotFoundException("Pinjaman dengan id " + addRequest.pinjamanId + " tidak ditemukan"));

            trx.setPinjaman(pinjaman);
        }

        PinjamanTransactionEntity saved = pinjamanTransactionRepository.save(trx);

        saved.setKodeTransaksi(String.format("TRX-%d-%05d", LocalDate.now().getYear(), trx.getTransPinjamanId()));

        saved = pinjamanTransactionRepository.save(saved);

        return new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(
                saved.getTransPinjamanId(),
                saved.getKodeTransaksi(),
                saved.getCustomer().getCustomerId(),
                saved.getCustomer().getUserName(),
                saved.getPinjaman() != null ? saved.getPinjaman().getPinjamanId() : null,
                saved.getTanggalPengajuan(),
                saved.getTanggalReview(),
                saved.getTanggalApproval(),
                saved.getNominalPinjaman(),
                saved.getTenor(),
                saved.getStatusPengajuan(),
                saved.getNoteMarketing(),
                saved.getNoteBm(),
                saved.getNoteBackOffice(),
                saved.getLastUpdate(),
                saved.getLastUpdateBy() != null ? saved.getLastUpdateBy().getEmployeeId() : null
        );
    }

        //ambil role dari JWT yang sedang aktif -> untuk validasi status yang boleh proceed pinjaman
    private void validateStatusTransition(String statusPengajuan) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AppUser appUser)) {
            return;
        }

        String role = appUser.getRole();
        if ("SUPERADMIN".equals(role)) {
            return;
        }

        if(role == null) {
            throw new AccessDeniedException("Sesi Invalid: Harap Login Kembali");
        }

        if ("Direview".equals(statusPengajuan) && !"MARKETING".equals(role)) {
            throw new AccessDeniedException("Cuma role MARKETING yang boleh submit review pengajuan");
        }

        if (("Disetujui".equals(statusPengajuan) || "Ditolak".equals(statusPengajuan)) && !"BRANCH_MANAGER".equals(role)) {
            throw new AccessDeniedException("Cuma role BRANCH_MANAGER yang boleh approve/reject pengajuan");
        }
    }

    @Override
    @Transactional
    public void updatePinjamanTransaction(Integer id, Integer customerId, Integer pinjamanId, Integer nominalPinjaman, Integer tenor,
                                           String statusPengajuan, LocalDate tanggalReview, LocalDate tanggalApproval,
                                           String noteMarketing, String noteBm, String noteBackOffice, Integer lastUpdateBy)
        {

        validateStatusTransition(statusPengajuan);

        Optional<PinjamanTransactionEntity> trxOpt = pinjamanTransactionRepository.findById(id);

        if (trxOpt.isEmpty()) {
            throw new EntityNotFoundException("pinjaman transaction id tidak ditemukan");
        }

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + customerId + " tidak ditemukan"));

        PinjamanTransactionEntity trxUpdate = trxOpt.get();
        trxUpdate.setCustomer(customer);
        trxUpdate.setNominalPinjaman(nominalPinjaman);
        trxUpdate.setTenor(tenor);
        trxUpdate.setStatusPengajuan(statusPengajuan);
        trxUpdate.setTanggalReview(tanggalReview);
        trxUpdate.setTanggalApproval(tanggalApproval);
        trxUpdate.setNoteMarketing(noteMarketing);
        trxUpdate.setNoteBm(noteBm);
        trxUpdate.setNoteBackOffice(noteBackOffice);
        trxUpdate.setLastUpdate(LocalDate.now());

        if (pinjamanId != null) {
            PinjamanEntity pinjaman = pinjamanRepository.findById(pinjamanId)
                    .orElseThrow(() -> new EntityNotFoundException("Pinjaman dengan id " + pinjamanId + " tidak ditemukan"));
            trxUpdate.setPinjaman(pinjaman);
        }

        if (lastUpdateBy != null) {
            EmployeEntity employee = employeRepository.findById(lastUpdateBy)
                    .orElseThrow(() -> new EntityNotFoundException("Employee dengan id " + lastUpdateBy + " tidak ditemukan"));
            trxUpdate.setLastUpdateBy(employee);
        }

        pinjamanTransactionRepository.save(trxUpdate);
    }

    @Override
    public String deletePinjamanTransaction(Integer id) {
        PinjamanTransactionEntity trxDelete = pinjamanTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("pinjaman transaction id: " + id + " tidak ditemukan"));

        pinjamanTransactionRepository.delete(trxDelete);

        return "pinjaman transaction dengan ID: " + id + " Telah di hapus";
    }
}
