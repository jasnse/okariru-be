package com.project.binar.okariru.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "role_group", schema = "core")
@Getter
@Setter
@NoArgsConstructor

public class rolegroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "role_group_id", nullable = false)
    private int roleGroupId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    private roleEntity role;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    private employeEntity employee;

    @Column(name = "nama_group_role")
    private String namaGroupRole;

    @Column(name = "created_at")
    private LocalDate createdAt;
}
