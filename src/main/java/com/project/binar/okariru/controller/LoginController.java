package com.project.binar.okariru.controller;

import com.project.binar.okariru.dto.LoginDTO;
import com.project.binar.okariru.service.jwtAuth.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login", description = "Autentikasi karyawan dan customer, menghasilkan JWT token. Endpoint publik (tidak butuh token).")
@RestController
@RequestMapping("api/v1/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "Login staff", description = "Login untuk karyawan (MARKETING, BRANCH_MANAGER, BACKOFFICE, SUPERADMIN) menggunakan username dan password, mengembalikan JWT token. Endpoint publik.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login berhasil", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginDTO.loginResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "username": "budi",
                              "role": "MARKETING",
                              "token": "eyJhbGciOiJIUzI1NiJ9...",
                              "userId": 3
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Email wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Username atau password salah", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Username atau password salah")))
    })
    @PostMapping("/employe")
    public ResponseEntity<LoginDTO.loginResponse> loginKaryawan( @RequestBody LoginDTO.loginRequest logReq){
        LoginDTO.loginResponse loginResponse = loginService.loginEmploye(logReq);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @Operation(summary = "Login customer", description = "Login untuk customer/nasabah menggunakan username dan password, mengembalikan JWT token. Endpoint publik.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login berhasil", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginDTO.loginResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "username": "andi123",
                              "role": "CUSTOMER",
                              "token": "eyJhbGciOiJIUzI1NiJ9...",
                              "userId": 10
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Validasi request body gagal", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "password wajib diisi"))),
            @ApiResponse(responseCode = "401", description = "Username atau password salah", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject(value = "Username atau password salah")))
    })
    @PostMapping("/customer")
    public ResponseEntity<LoginDTO.loginResponse> loginCustomer (@RequestBody LoginDTO.loginRequest logReq){
        LoginDTO.loginResponse loginResponse = loginService.loginCustomer(logReq);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

}
