package com.project.binar.okariru.service.jwtAuth;


import com.project.binar.okariru.entity.AppUser;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.EmployeRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final EmployeRepository employe;
    private final CustomerRepository customer;

//    @NotNull
//    @Override
//    public AppUser loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
//        Optional<AppUser> optionalKaryawan = findKaryawan(username);
//        return optionalKaryawan
//                .orElseThrow(() -> new UsernameNotFoundException("karyawan dengan username " + username + " tidak ditemukan"));
//    }

    @Override
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return findKaryawan(username)
                .or(() -> findCustomer(username))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User dengan username " + username + " tidak ditemukan"));
    }

    @NotNull

    public Optional<AppUser> findKaryawan(String username){
        return employe.findByUsernameWithRoles(username)
                .filter(karyawan -> karyawan.getPassword() != null)
                .map(this::toAppUser);
    }

    public Optional<AppUser> findCustomer(String username) {
        return customer.findByUserName(username)
                .filter(cust -> cust.getPassword() != null)
                .map(this::toAppUserCust);
    }


    private AppUser toAppUser(EmployeEntity karyawan){
//        return new AppUser(karyawan.getEmail(), karyawan.getPassword(), karyawan.get);
        List<String> roles = karyawan.getRoleGroups() != null ?
                karyawan.getRoleGroups().stream()
                        .filter(rg -> rg.getRole() != null)
                        .map(rg -> rg.getRole().getNamaRole())
                        .toList() : List.of();
        String pickRole = roles.isEmpty() ? null : roles.get(0);
        return new AppUser(
                karyawan.getUserName(),
                karyawan.getPassword(),
                pickRole
        );
    }

    private AppUser toAppUserCust(CustomerEntity customer) {
        return new AppUser(
                customer.getUserName(),
                customer.getPassword(),
                customer.getRoleCustomer()
        );
    }
}
