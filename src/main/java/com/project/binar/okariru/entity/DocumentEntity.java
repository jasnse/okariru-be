package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "dokumen_pengajuan", schema = "core")
@Getter
@Setter
@NoArgsConstructor

public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "dokumen_id")
    private int dokumenId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trans_pinjaman_id", referencedColumnName = "trans_pinjaman_id")
    private PinjamanTransactionEntity transPinjaman;

    @Column(name = "nama_file")
    private String namaFile;

    @Column(name = "pathfile")
    private String pathfile;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploadBy", referencedColumnName = "customer_id")
    private CustomerEntity uploadBy;

    @Column(name = "upload_date")
    private LocalDate uploadDate;


    @Column(name = "status_verification")
    private String statusVerification;
}
