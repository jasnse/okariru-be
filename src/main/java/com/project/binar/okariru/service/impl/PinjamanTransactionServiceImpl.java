package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.PinjamanTransactionServiceRequest;
import com.project.binar.okariru.dto.PinjamanTransactionServiceResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.PinjamanEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.PinjamanRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import com.project.binar.okariru.service.PinjamanTransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Override
    public List<PinjamanTransactionServiceResponse.getPinjamanTransactionResponse> getAllPinjamanTransaction() {
        return pinjamanTransactionRepository.findAll()
                .stream()
                .map(trx -> new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(
                        trx.getTransPinjamanId(),
                        trx.getCustomer().getCustomerId(),
                        trx.getPinjaman() != null ? trx.getPinjaman().getPinjamanId() : null,
                        trx.getTanggalPengajuan(),
                        trx.getTanggalReview(),
                        trx.getTanggalApproval(),
                        trx.getNominalPinjaman(),
                        trx.getStatusPengajuan(),
                        trx.getNoteApproval(),
                        trx.getRejectNote(),
                        trx.getLastUpdate(),
                        trx.getLastUpdateBy() != null ? trx.getLastUpdateBy().getEmployeeId() : null
                ))
                .toList();
    }

    @Override
    public PinjamanTransactionServiceResponse.getPinjamanTransactionResponse getPinjamanTransactionById(Integer id) {
        PinjamanTransactionEntity trx = pinjamanTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("pinjaman transaction dengan Id " + id + " tidak ditemukan"));
        return new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(
                trx.getTransPinjamanId(),
                trx.getCustomer().getCustomerId(),
                trx.getPinjaman() != null ? trx.getPinjaman().getPinjamanId() : null,
                trx.getTanggalPengajuan(),
                trx.getTanggalReview(),
                trx.getTanggalApproval(),
                trx.getNominalPinjaman(),
                trx.getStatusPengajuan(),
                trx.getNoteApproval(),
                trx.getRejectNote(),
                trx.getLastUpdate(),
                trx.getLastUpdateBy() != null ? trx.getLastUpdateBy().getEmployeeId() : null
        );
    }

    @Override
    @Transactional
    public PinjamanTransactionServiceResponse.getPinjamanTransactionResponse addPinjamanTransaction(PinjamanTransactionServiceRequest.pinjamanTransactionAddRequest addRequest) {
        CustomerEntity customer = customerRepository.findById(addRequest.customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + addRequest.customerId + " tidak ditemukan"));

        PinjamanTransactionEntity trx = new PinjamanTransactionEntity();
        trx.setCustomer(customer);
        trx.setNominalPinjaman(addRequest.nominalPinjaman);
        trx.setTanggalPengajuan(LocalDate.now());
        trx.setStatusPengajuan("Pengajuan");

        if (addRequest.pinjamanId != null) {
            PinjamanEntity pinjaman = pinjamanRepository.findById(addRequest.pinjamanId)
                    .orElseThrow(() -> new EntityNotFoundException("Pinjaman dengan id " + addRequest.pinjamanId + " tidak ditemukan"));
            trx.setPinjaman(pinjaman);
        }

        PinjamanTransactionEntity saved = pinjamanTransactionRepository.save(trx);
        return new PinjamanTransactionServiceResponse.getPinjamanTransactionResponse(
                saved.getTransPinjamanId(),
                saved.getCustomer().getCustomerId(),
                saved.getPinjaman() != null ? saved.getPinjaman().getPinjamanId() : null,
                saved.getTanggalPengajuan(),
                saved.getTanggalReview(),
                saved.getTanggalApproval(),
                saved.getNominalPinjaman(),
                saved.getStatusPengajuan(),
                saved.getNoteApproval(),
                saved.getRejectNote(),
                saved.getLastUpdate(),
                saved.getLastUpdateBy() != null ? saved.getLastUpdateBy().getEmployeeId() : null
        );
    }

    @Override
    @Transactional
    public void updatePinjamanTransaction(Integer id, Integer customerId, Integer pinjamanId, Integer nominalPinjaman,
                                           String statusPengajuan, LocalDate tanggalReview, LocalDate tanggalApproval,
                                           String noteApproval, String rejectNote, Integer lastUpdateBy) {
        Optional<PinjamanTransactionEntity> trxOpt = pinjamanTransactionRepository.findById(id);

        if (trxOpt.isEmpty()) {
            throw new EntityNotFoundException("pinjaman transaction id tidak ditemukan");
        }

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + customerId + " tidak ditemukan"));

        PinjamanTransactionEntity trxUpdate = trxOpt.get();
        trxUpdate.setCustomer(customer);
        trxUpdate.setNominalPinjaman(nominalPinjaman);
        trxUpdate.setStatusPengajuan(statusPengajuan);
        trxUpdate.setTanggalReview(tanggalReview);
        trxUpdate.setTanggalApproval(tanggalApproval);
        trxUpdate.setNoteApproval(noteApproval);
        trxUpdate.setRejectNote(rejectNote);
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
