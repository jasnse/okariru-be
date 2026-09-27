package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pinjaman_transaction", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class PinjamanTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "trans_pinjaman_id")
    private int transPinjamanId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "pinjaman_id", referencedColumnName = "pinjaman_id")
    private PinjamanEntity pinjaman;

    @Column(name = "tanggal_pengajuan")
    private LocalDate tanggalPengajuan;

    @Column(name = "tanggal_review")
    private LocalDate tanggalReview;

    @Column(name = "tanggal_approval")
    private LocalDate tanggalApproval;

    @Column(name = "nominal_pinjaman")
    private Integer nominalPinjaman;

    @Column(name = "tenor")
    private Integer tenor;

    @Column(name = "status_pengajuan")
    private String statusPengajuan;

    @Column(name = "note_marketing")
    private String noteMarketing;

    @Column(name = "note_bm")
    private String noteBm;

    @Column(name = "note_backoffice")
    private String noteBackOffice;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @Column(name = "kode_transaksi", unique = true)
    private String kodeTransaksi;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "last_update_by_id", referencedColumnName = "employee_id")
    private EmployeEntity lastUpdateBy;

    // Snapshot data customer & pinjaman PADA SAAT pengajuan dibuat.
    // Sengaja disalin (bukan diambil live dari relasi customer/pinjaman) supaya kalau customer
    // update profil atau admin ubah master data pinjaman di tengah proses, data yang sudah
    // terlanjur diajukan/direview/di-approve tidak ikut berubah.
    @Column(name = "snapshot_customer_name")
    private String snapshotCustomerName;

    @Column(name = "snapshot_customer_nik")
    private String snapshotCustomerNik;

    @Column(name = "snapshot_customer_tempat_lahir")
    private String snapshotCustomerTempatLahir;

    @Column(name = "snapshot_customer_tanggal_lahir")
    private LocalDate snapshotCustomerTanggalLahir;

    @Column(name = "snapshot_customer_gender")
    private String snapshotCustomerGender;

    @Column(name = "snapshot_customer_alamat")
    private String snapshotCustomerAlamat;

    @Column(name = "snapshot_customer_pekerjaan")
    private String snapshotCustomerPekerjaan;

    @Column(name = "snapshot_customer_pendapatan")
    private Integer snapshotCustomerPendapatan;

    @Column(name = "snapshot_customer_marital_status")
    private String snapshotCustomerMaritalStatus;

    @Column(name = "snapshot_customer_no_rekening")
    private String snapshotCustomerNoRekening;

    @Column(name = "snapshot_jenis_pinjaman")
    private String snapshotJenisPinjaman;

    @Column(name = "snapshot_deskripsi_pinjaman")
    private String snapshotDeskripsiPinjaman;

    @Column(name = "snapshot_bunga")
    private Double snapshotBunga;

    @Column(name = "snapshot_biaya_lainnya")
    private Double snapshotBiayaLainnya;

}
