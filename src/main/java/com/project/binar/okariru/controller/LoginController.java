package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.LoginDTO;
import com.project.binar.okariru.service.jwtAuth.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/employe")
    public ResponseEntity<LoginDTO.loginResponse> loginKaryawan( @RequestBody LoginDTO.loginRequest logReq){
        LoginDTO.loginResponse loginResponse = loginService.loginEmploye(logReq);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @PostMapping("/customer")
    public ResponseEntity<LoginDTO.loginResponse> loginCustomer (@RequestBody LoginDTO.loginRequest logReq){
        LoginDTO.loginResponse loginResponse = loginService.loginCustomer(logReq);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

}
