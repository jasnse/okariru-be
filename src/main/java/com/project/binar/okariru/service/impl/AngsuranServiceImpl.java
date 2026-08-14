package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.AngsuranRequest;
import com.project.binar.okariru.dto.AngsuranResponse;
import com.project.binar.okariru.entity.AngsuranEntity;
import com.project.binar.okariru.entity.PinjamanTransactionEntity;
import com.project.binar.okariru.repository.AngsuranRepository;
import com.project.binar.okariru.repository.PinjamanTransactionRepository;
import com.project.binar.okariru.service.AngsuranService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AngsuranServiceImpl implements AngsuranService {

    private final AngsuranRepository angsuranRepository;
    private final PinjamanTransactionRepository pinjamanTransactionRepository;

    @Override
    public List<AngsuranResponse.getAngsuranResponse> getAllAngsuran() {
        return angsuranRepository.findAll()
                .stream()
                .map(angsuran -> new AngsuranResponse.getAngsuranResponse(
                        angsuran.getAngsuranId(),
                        angsuran.getTransPinjaman().getTransPinjamanId(),
                        angsuran.getJumlahPokok(),
                        angsuran.getJumlahBunga(),
                        angsuran.getTotalAngsuran(),
                        angsuran.getTanggalJatuhTempo(),
                        angsuran.getStatusAngsuran(),
                        angsuran.getTenor(),
                        angsuran.getSisaTagihan()
                ))
                .toList();
    }

    @Override
    public AngsuranResponse.getAngsuranResponse getAngsuranById(Integer id) {
        AngsuranEntity angsuran = angsuranRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("angsuran dengan Id " + id + " tidak ditemukan"));
        return new AngsuranResponse.getAngsuranResponse(
                angsuran.getAngsuranId(),
                angsuran.getTransPinjaman().getTransPinjamanId(),
                angsuran.getJumlahPokok(),
                angsuran.getJumlahBunga(),
                angsuran.getTotalAngsuran(),
                angsuran.getTanggalJatuhTempo(),
                angsuran.getStatusAngsuran(),
                angsuran.getTenor(),
                angsuran.getSisaTagihan()
        );
    }

    @Override
    @Transactional
    public AngsuranResponse.getAngsuranResponse addAngsuran(AngsuranRequest.angsuranAddRequest addRequest) {
        PinjamanTransactionEntity transPinjaman = pinjamanTransactionRepository.findById(addRequest.transPinjamanId)
                .orElseThrow(() -> new EntityNotFoundException("Pinjaman transaction dengan id " + addRequest.transPinjamanId + " tidak ditemukan"));

        AngsuranEntity angsuran = new AngsuranEntity();
        angsuran.setTransPinjaman(transPinjaman);
        angsuran.setJumlahPokok(addRequest.jumlahPokok);
        angsuran.setJumlahBunga(addRequest.jumlahBunga);
        angsuran.setTotalAngsuran(addRequest.totalAngsuran);
        angsuran.setTanggalJatuhTempo(addRequest.tanggalJatuhTempo);
        angsuran.setStatusAngsuran(addRequest.statusAngsuran);
        angsuran.setTenor(addRequest.tenor);
        angsuran.setSisaTagihan(addRequest.totalAngsuran);

        AngsuranEntity saved = angsuranRepository.save(angsuran);
        return new AngsuranResponse.getAngsuranResponse(
                saved.getAngsuranId(),
                saved.getTransPinjaman().getTransPinjamanId(),
                saved.getJumlahPokok(),
                saved.getJumlahBunga(),
                saved.getTotalAngsuran(),
                saved.getTanggalJatuhTempo(),
                saved.getStatusAngsuran(),
                saved.getTenor(),
                saved.getSisaTagihan()
        );
    }

    //generate angsuran berdasarkan tenor yang di pilih saat pinjaman
    @Override
    @Transactional
    public List<AngsuranResponse.getAngsuranResponse> generateAngsuran(AngsuranRequest.angsuranGenerateRequest generateRequest) {

        //get pinjman by id, validasi pinjaman transaction
        PinjamanTransactionEntity transPinjaman = pinjamanTransactionRepository.findById(generateRequest.transPinjamanId)
                .orElseThrow(() -> new EntityNotFoundException("Pinjaman transaction dengan id " + generateRequest.transPinjamanId + " tidak ditemukan"));

        if (transPinjaman.getPinjaman() == null) {
            throw new EntityNotFoundException("Pinjaman transaction ini belum terhubung dengan produk pinjaman, bunga tidak diketahui");
        }

        if (transPinjaman.getNominalPinjaman() == null) {
            throw new EntityNotFoundException("Nominal pinjaman belum diisi di pinjaman transaction ini");
        }

        //hitung bunga dan pokok berdasarkan tenor dan rate bunga
        int tenor = generateRequest.tenor;
        long nominalPinjaman = transPinjaman.getNominalPinjaman();
        double bungaRate = transPinjaman.getPinjaman().getBunga() != null ? transPinjaman.getPinjaman().getBunga() : 0.0;

        long pokokPerBulan = nominalPinjaman / tenor;
        double bungaPerBulan = (nominalPinjaman * bungaRate) / tenor;

        LocalDate tanggalMulai = transPinjaman.getTanggalApproval() != null ? transPinjaman.getTanggalApproval() : LocalDate.now();

        //insert table angsuran berdasarkan jumlah tenor
        List<AngsuranEntity> angsuranList = new ArrayList<>();
        for (int i = 1; i <= tenor; i++) {
            AngsuranEntity angsuran = new AngsuranEntity();
            angsuran.setTransPinjaman(transPinjaman);
            angsuran.setJumlahPokok(pokokPerBulan);
            angsuran.setJumlahBunga(bungaPerBulan);
            int total = (int) Math.round(pokokPerBulan + bungaPerBulan);
            angsuran.setTotalAngsuran(total);
            angsuran.setTanggalJatuhTempo(tanggalMulai.plusMonths(i));
            angsuran.setStatusAngsuran("Belum Bayar");
            angsuran.setTenor(i);
            angsuran.setSisaTagihan(total);
            angsuranList.add(angsuran);
        }

        List<AngsuranEntity> saved = angsuranRepository.saveAll(angsuranList);

        return saved.stream()
                .map(angsuran -> new AngsuranResponse.getAngsuranResponse(
                        angsuran.getAngsuranId(),
                        angsuran.getTransPinjaman().getTransPinjamanId(),
                        angsuran.getJumlahPokok(),
                        angsuran.getJumlahBunga(),
                        angsuran.getTotalAngsuran(),
                        angsuran.getTanggalJatuhTempo(),
                        angsuran.getStatusAngsuran(),
                        angsuran.getTenor(),
                        angsuran.getSisaTagihan()
                ))
                .toList();
    }

    @Override
    @Transactional
    public AngsuranResponse.angsuranBayarResponse bayarAngsuran(AngsuranRequest.angsuranBayarRequest bayarRequest) {

        //validasi pinjaman transaction
        PinjamanTransactionEntity transPinjaman = pinjamanTransactionRepository
                .findById(bayarRequest.transPinjamanId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pinjaman transaction dengan id "
                                + bayarRequest.transPinjamanId
                                + " tidak ditemukan"));

        //get pinjaman transaction
        List<AngsuranEntity> angsuranList = angsuranRepository
                .findByTransPinjamanOrderByTenorAsc(transPinjaman);

        if (angsuranList.isEmpty()) {
            throw new EntityNotFoundException("Belum ada angsuran untuk pinjaman transaction ini");
        }

        // hitung total angsuran dari pinjaman tersebut (all tenor)
        int maxBayar = angsuranList.stream()
                .mapToInt(AngsuranEntity::getSisaTagihan)
                .sum();

        //validasi nominal yang di input untuk  bayar
        int nominalBayar = bayarRequest.nominalBayar;
        if (nominalBayar > maxBayar) {
            throw new IllegalArgumentException("Nominal bayar melebihi total seluruh sisa tagihan (maksimal " + maxBayar + ")");
        }

        int deposit = nominalBayar;
        int jumlahLunas = 0;

        //looping ke semua tenor angsuran, mengurangi nominal bayarnya setiap iterasi
        for (AngsuranEntity angsuran : angsuranList) {

            if (deposit <= 0) {
                break;
            }


            if (angsuran.getSisaTagihan() <= 0) {
                continue;
            }

            if (deposit >= angsuran.getSisaTagihan()) {
                angsuran.setSisaTagihan(0);
                angsuran.setStatusAngsuran("Lunas");
                deposit -= angsuran.getSisaTagihan();
                jumlahLunas++;
            } else {
                angsuran.setSisaTagihan(angsuran.getSisaTagihan() - deposit);
                angsuran.setStatusAngsuran("Kurang Bayar");
                deposit = 0;
            }

            angsuranRepository.save(angsuran);
        }

        AngsuranResponse.angsuranBayarResponse resp = new AngsuranResponse.angsuranBayarResponse();
        resp.setMessage("Pembayaran berhasil, " + jumlahLunas + " angsuran Lunas dari total pembayaran " + nominalBayar);
        return resp;
    }

    @Override
    @Transactional
    public void updateAngsuran(Integer id, Integer transPinjamanId, Long jumlahPokok, Double jumlahBunga,
                                Integer totalAngsuran, LocalDate tanggalJatuhTempo, String statusAngsuran, Integer tenor) {
        Optional<AngsuranEntity> angsuranOpt = angsuranRepository.findById(id);

        if (angsuranOpt.isEmpty()) {
            throw new EntityNotFoundException("angsuran id tidak ditemukan");
        }

        PinjamanTransactionEntity transPinjaman = pinjamanTransactionRepository.findById(transPinjamanId)
                .orElseThrow(() -> new EntityNotFoundException("Pinjaman transaction dengan id " + transPinjamanId + " tidak ditemukan"));

        AngsuranEntity angsuranUpdate = angsuranOpt.get();
        angsuranUpdate.setTransPinjaman(transPinjaman);
        angsuranUpdate.setJumlahPokok(jumlahPokok);
        angsuranUpdate.setJumlahBunga(jumlahBunga);
        angsuranUpdate.setTotalAngsuran(totalAngsuran);
        angsuranUpdate.setTanggalJatuhTempo(tanggalJatuhTempo);
        angsuranUpdate.setStatusAngsuran(statusAngsuran);
        angsuranUpdate.setTenor(tenor);

        angsuranRepository.save(angsuranUpdate);
    }

    @Override
    public String deleteAngsuran(Integer id) {
        AngsuranEntity angsuranDelete = angsuranRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("angsuran id: " + id + " tidak ditemukan"));

        angsuranRepository.delete(angsuranDelete);

        return "angsuran dengan ID: " + id + " Telah di hapus";
    }
}
