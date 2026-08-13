package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "notification", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "notif_id")
    private int notifId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private CustomerEntity customer;

    @Column(name = "isi_notifikasi")
    private String isiNotifikasi;

    @Column(name = "tanggal_notifikasi")
    private LocalDate tanggalNotifikasi;
}
