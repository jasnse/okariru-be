package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Table(name = "mst_employee", schema = "core")
@Setter
@Getter
@Entity
public class employeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private int EmployeeId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nip")
    private String nip;

    @Column(name = "joined_date")
    private LocalDate joinedDate;
}
