package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "mst_employee", schema = "core")
@Setter
@Getter
@Entity
@NoArgsConstructor
public class EmployeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "employee_id")
    private int employeeId;

    @NotBlank(message = "username gak boleh kosong")
    @Column(name = "user_name")
    private String userName;

    @NotBlank(message = "email gak boleh kosong")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "password gak boleh kosong")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "nip gak boleh kosong")
    @Column(name = "nip")
    private String nip;

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_group_id")
    private RolegroupEntity roleGroup;
}
