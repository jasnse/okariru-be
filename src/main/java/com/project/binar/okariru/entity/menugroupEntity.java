package com.project.binar.okariru.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "menu_group", schema = "core")
@Getter
@Setter
@NoArgsConstructor
public class menugroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "menu_group_id", nullable = false)
    private int menuGroupId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", referencedColumnName = "menu_id", nullable = false)
    private menuEntity menu;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "role_group_id", nullable = false)
    private rolegroupEntity role;

    @Column(name = "nama_group_menu")
    private String namaGroupMenu;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;
}
