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

}
