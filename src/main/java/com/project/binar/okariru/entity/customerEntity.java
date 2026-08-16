package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mst_customer", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "customer_id", nullable = false)
    private int customerId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "sid_name")
    private String sidName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nik")
    private String nik;

    @Column(name = "tempat_lahir")
    private String tempatLahir;

    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;

    @Column(name = "alamat")
    private String alamat;

    @Column(name = "pekerjaan")
    private String pekerjaan;

    @Column(name = "pendapatan")
    private Integer pendapatan;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "gender")
    private String gender;

    @Column(name = "no_rekening")
    private String noRekening;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "role_customer")
    private String roleCustomer;
}
