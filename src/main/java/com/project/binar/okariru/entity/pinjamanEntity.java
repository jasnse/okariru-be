package com.project.binar.okariru.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mst_pinjaman", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class pinjamanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "pinjaman_id", nullable = false)
    private int pinjamanId;

    @Column(name = "jenis_pinjaman")
    private String jenisPinjaman;

    @Column(name = "deskripsi_pinjaman")
    private String deskripsiPinjaman;

    @Column(name = "bunga")
    private Double bunga;

    @Column(name = "biaya_lainnya")
    private Double biayaLainnya;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;
}
