package com.project.binar.okariru.service.jwtAuth;

import com.project.binar.okariru.entity.AppUser;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.RoleEntity;
import com.project.binar.okariru.entity.RolegroupEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.EmployeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private EmployeRepository employe;
    @Mock
    private CustomerRepository customer;

    @InjectMocks
    private AppUserDetailsService service;

    private EmployeEntity employee;
    private CustomerEntity cust;

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity();
        role.setNamaRole("ADMIN");
        RolegroupEntity group = new RolegroupEntity();
        group.setRole(role);

        employee = new EmployeEntity();
        employee.setUserName("budi");
        employee.setPassword("HASH-EMP");
        employee.setRoleGroup(group);

        cust = new CustomerEntity();
        cust.setCustomerId(7);
        cust.setUserName("andi");
        cust.setPassword("HASH-CUST");
        cust.setRoleCustomer("CUSTOMER");
    }

    // ---------- findKaryawan ----------

    @Test
    void findKaryawan_rolediAmbilDariRoleGroup() {
        when(employe.findByUsernameWithRoles("budi")).thenReturn(Optional.of(employee));

        AppUser user = service.findKaryawan("budi").orElseThrow();

        assertEquals("budi", user.getUsername());
        assertEquals("HASH-EMP", user.getPassword());
        assertEquals("ADMIN", user.getRole());
        assertNull(user.getUserId());
    }

    @Test
    void findKaryawan_tanpaRoleGroup_roleNull() {
        employee.setRoleGroup(null);
        when(employe.findByUsernameWithRoles("budi")).thenReturn(Optional.of(employee));

        assertNull(service.findKaryawan("budi").orElseThrow().getRole());
    }

    @Test
    void findKaryawan_roleGroupTanpaRole_roleNull() {
        employee.getRoleGroup().setRole(null);
        when(employe.findByUsernameWithRoles("budi")).thenReturn(Optional.of(employee));

        assertNull(service.findKaryawan("budi").orElseThrow().getRole());
    }

    @Test
    void findKaryawan_passwordNull_difilter() {
        employee.setPassword(null);
        when(employe.findByUsernameWithRoles("budi")).thenReturn(Optional.of(employee));

        assertTrue(service.findKaryawan("budi").isEmpty());
    }

    @Test
    void findKaryawan_tidakDitemukan() {
        when(employe.findByUsernameWithRoles("budi")).thenReturn(Optional.empty());

        assertTrue(service.findKaryawan("budi").isEmpty());
    }

    // ---------- findCustomer ----------

    @Test
    void findCustomer_berhasil() {
        when(customer.findByUserName("andi")).thenReturn(Optional.of(cust));

        AppUser user = service.findCustomer("andi").orElseThrow();

        assertEquals("CUSTOMER", user.getRole());
        assertEquals(7, user.getUserId());
    }

    @Test
    void findCustomer_passwordNull_difilter() {
        cust.setPassword(null);
        when(customer.findByUserName("andi")).thenReturn(Optional.of(cust));

        assertTrue(service.findCustomer("andi").isEmpty());
    }

    @Test
    void findCustomer_tidakDitemukan() {
        when(customer.findByUserName("andi")).thenReturn(Optional.empty());

        assertTrue(service.findCustomer("andi").isEmpty());
    }

    // ---------- loadUserByUsername ----------

    @Test
    void loadUserByUsername_karyawanDidahulukan() {
        when(employe.findByUsernameWithRoles("budi")).thenReturn(Optional.of(employee));

        AppUser user = service.loadUserByUsername("budi");

        assertEquals("ADMIN", user.getRole());
        verifyNoInteractions(customer);
    }

    @Test
    void loadUserByUsername_fallbackKeCustomer() {
        when(employe.findByUsernameWithRoles("andi")).thenReturn(Optional.empty());
        when(customer.findByUserName("andi")).thenReturn(Optional.of(cust));

        AppUser user = service.loadUserByUsername("andi");

        assertEquals(7, user.getUserId());
    }

    @Test
    void loadUserByUsername_tidakDitemukanDiKeduanya() {
        when(employe.findByUsernameWithRoles("zzz")).thenReturn(Optional.empty());
        when(customer.findByUserName("zzz")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("zzz"));
        assertTrue(ex.getMessage().contains("zzz"));
    }
}
