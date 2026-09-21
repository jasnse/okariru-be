package com.project.binar.okariru.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "role_group", schema = "core")
@Getter
@Setter
@NoArgsConstructor

public class RolegroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "role_group_id", nullable = false)
    private int roleGroupId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    private RoleEntity role;

    @Column(name = "nama_group_role")
    private String namaGroupRole;

    //inverse EmployeEntity.roleGroup
    @OneToMany(mappedBy = "roleGroup", fetch = FetchType.LAZY)
    private List<EmployeEntity> employees;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;
}
