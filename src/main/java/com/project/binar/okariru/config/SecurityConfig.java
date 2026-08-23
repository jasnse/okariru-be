package com.project.binar.okariru.config;

import com.project.binar.okariru.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {




    @Value("${app.security.cors-allowed-origin}")
    private List<String> AllowedOrigins;

    @Bean
    AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            String message = authException instanceof BadCredentialsException
                    ? "Username atau password salah"
                    : "Unauthorized - silakan login terlebih dahulu";

            response.getWriter().write("{\"message\": \"" + message + "\"}");
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter
    ) throws Exception {
        return  http
                .csrf( csrfConfigurer -> csrfConfigurer.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headersConfigurer -> headersConfigurer
                        .referrerPolicy( referrerPolicy -> referrerPolicy.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .frameOptions(frameOptionsConfig -> frameOptionsConfig.deny()))
                .authorizeHttpRequests(request -> request

                        // ===== PUBLIC =====
                        .requestMatchers(HttpMethod.POST,"/api/v1/login/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/customer/**").permitAll() //register
                        .requestMatchers(HttpMethod.POST, "/api/v1/reset/**").permitAll() //forgot password


//                        .requestMatchers("/api/v1/reset/**").hasRole("CUSTOMER")


                        // ===== PINJAMAN =====
                        .requestMatchers(HttpMethod.POST,"/api/v1/pinjaman").hasRole("BACKOFFICE")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/pinjaman").hasRole("BACKOFFICE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pinjaman")
                            .hasAnyRole("MARKETING", "BRANCH_MANAGER", "BACKOFFICE", "CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/pinjaman").hasRole("BACKOFFICE")

                        // ===== PLAFOND =====
                        .requestMatchers(HttpMethod.POST,"/api/v1/plafond").hasRole("BACKOFFICE")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/plafond").hasRole("BACKOFFICE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/plafond")
                            .hasAnyRole("BACKOFFICE", "CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/plafond").hasRole("BACKOFFICE")

                        // ===== TRANSAKSI PINJAMAN =====
                        .requestMatchers(HttpMethod.POST, "/api/v1/pinjaman/transaction/**")
                            .hasAnyRole( "MARKETING", "CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pinjaman/transaction/**")
                            .hasAnyRole( "MARKETING","BRANCH_MANAGER", "BACKOFFICE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pinjaman/transaction/**")
                            .hasAnyRole("MARKETING", "BRANCH_MANAGER", "BACKOFFICE", "CUSTOMER")

                        // ===== ANGSURAN  =====
                        .requestMatchers(HttpMethod.GET, "/api/v1/angsuran")
                            .hasAnyRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/angsuran/generate")
                            .hasAnyRole("BACKOFFICE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/angsuran/bayar")
                        .hasAnyRole("CUSTOMER")

                        // ===== DOKUMEN  =====
                        .requestMatchers(HttpMethod.GET, "/api/v1/document")
                            .hasAnyRole("MARKETING", "BRANCH_MANAGER", "BACKOFFICE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/document")
                            .hasAnyRole("CUSTOMER")


                        //set up employee and permission
                        .requestMatchers("/api/v1/employees").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/roles/**").hasRole("SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/menu/my-menu")
                            .hasAnyRole("SUPERADMIN", "MARKETING", "BRANCH_MANAGER", "BACKOFFICE")
                        .requestMatchers("/api/v1/menu/**").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/roleGroup/**").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/menugroup/**").hasRole("SUPERADMIN")

                        // ===== MASTER DATA =====
                        .requestMatchers("/api/v1/pinjaman/**").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/customer/**").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/plafond/**").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/pinjaman/transaction/**").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/angsuran/**").hasRole("SUPERADMIN")
                        .requestMatchers("/api/v1/notification/**").hasRole("SUPERADMIN") // blm tau
                        .requestMatchers("/api/v1/document/**").hasRole("SUPERADMIN")

                        .anyRequest().authenticated()

                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(unauthorizedEntryPoint()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration konfigurasi = new CorsConfiguration();
        konfigurasi.setAllowedOrigins(AllowedOrigins);
        konfigurasi.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        konfigurasi.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        konfigurasi.setAllowCredentials(true);
        konfigurasi.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource sumber = new UrlBasedCorsConfigurationSource();
        sumber.registerCorsConfiguration("/api/**", konfigurasi);
        return sumber;
    }

    // password encoder
    @Bean
    PasswordEncoder passwordEncoder() {
        String baku = "bcrypt";
        Map<String, PasswordEncoder> encoderMap = Map.of(baku, new BCryptPasswordEncoder(12));
        return new DelegatingPasswordEncoder(baku, encoderMap);
    }
}