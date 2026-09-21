package com.project.binar.okariru.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mst_plafond", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class PlafondEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "plafond_id", nullable = false)
    private int plafondId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "customer_id", nullable = false)
    private CustomerEntity user;

    @Column(name = "total_plafond")
    private Integer totalPlafond;

    @Column(name = "deskripsi_plafond")
    private String deskripsiPlafond;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "created_by", referencedColumnName = "employee_id")
    private EmployeEntity createdBy;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "updated_by", referencedColumnName = "employee_id")
    private EmployeEntity updatedBy;
}
