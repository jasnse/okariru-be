package com.project.binar.okariru.service.jwtAuth;


import com.project.binar.okariru.dto.LoginDTO;
import com.project.binar.okariru.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final AppUserDetailsService appUserDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginDTO.loginResponse loginEmploye(LoginDTO.loginRequest logReq){
        AppUser user = appUserDetailsService.findKaryawan(logReq.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username atau password salah"));

        if (!passwordEncoder.matches(logReq.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Username atau password salah");
        }

        String token = jwtService.issue(user, Instant.now());
        return new LoginDTO.loginResponse(user.getUsername(), user.getRole(), token, user.getUserId());
    }

    public LoginDTO.loginResponse loginCustomer(LoginDTO.loginRequest reqlog){
        AppUser customer = appUserDetailsService.findCustomer(reqlog.getUsername())
                .orElseThrow(() ->  new BadCredentialsException("Username atau password salah"));

        if (!passwordEncoder.matches(reqlog.getPassword(), customer.getPassword())) {
            throw new BadCredentialsException("Username atau password salah");
        }

        String token = jwtService.issue(customer, Instant.now());
        return new LoginDTO.loginResponse(customer.getUsername(), customer.getRole(), token, customer.getUserId());

    }
}
