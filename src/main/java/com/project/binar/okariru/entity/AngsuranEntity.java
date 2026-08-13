package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "angsuran", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class AngsuranEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "angsuran_id")
    private int angsuranId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trans_pinjaman_id", referencedColumnName = "trans_pinjaman_id", nullable = false)
    private PinjamanTransactionEntity transPinjaman;

    @Column(name = "jumlah_pokok")
    private Long jumlahPokok;

    @Column(name = "jumlah_bunga")
    private Double jumlahBunga;

    @Column(name = "total_angsuran")
    private Integer totalAngsuran;

    @Column(name = "tanggal_jatuh_tempo")
    private LocalDate tanggalJatuhTempo;

    @Column(name = "status_angsuran")
    private String statusAngsuran;

    @Column(name = "tenor")
    private Integer tenor;

    @Column(name = "sisa_tagihan")
    private Integer sisaTagihan;
}

