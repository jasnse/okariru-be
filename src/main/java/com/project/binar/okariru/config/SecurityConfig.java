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
import org.springframework.security.web.access.AccessDeniedHandler;
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


//1

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
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Anda tidak memiliki akses untuk melakukan aksi ini\"}");
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

                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html"
                                ).permitAll()
//                        .requestMatchers("/api/v1/reset/**").hasRole("CUSTOMER")


                        // ===== PINJAMAN =====
                        .requestMatchers(HttpMethod.POST,"/api/v1/pinjaman").hasAnyRole("SUPERADMIN", "BACKOFFICE")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/pinjaman").hasAnyRole("SUPERADMIN","BACKOFFICE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pinjaman")
                            .hasAnyRole("MARKETING", "BRANCH_MANAGER", "BACKOFFICE", "CUSTOMER", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/pinjaman").hasAnyRole("SUPERADMIN", "BACKOFFICE")

                        // ===== PLAFOND =====
                        .requestMatchers(HttpMethod.POST,"/api/v1/plafond").hasAnyRole("BACKOFFICE", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/plafond").hasAnyRole("BACKOFFICE", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/plafond")
                            .hasAnyRole("SUPERADMIN", "BACKOFFICE", "CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/plafond").hasRole("BACKOFFICE")

                        // ===== TRANSAKSI PINJAMAN =====
                        .requestMatchers(HttpMethod.POST, "/api/v1/pinjaman/transaction/**")
                            .hasAnyRole( "MARKETING", "CUSTOMER", "SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pinjaman/transaction/**")
                            .hasAnyRole( "MARKETING","BRANCH_MANAGER", "BACKOFFICE", "SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pinjaman/transaction/**")
                            .hasAnyRole("MARKETING", "BRANCH_MANAGER", "BACKOFFICE", "CUSTOMER", "SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/customer/**").hasAnyRole("MARKETING","BRANCH_MANAGER", "BACKOFFICE", "SUPERADMIN", "CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/customer/me").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/customer/fcm-token").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/customer/fcm-token").hasRole("CUSTOMER")

                        // ===== ANGSURAN  =====
                        .requestMatchers(HttpMethod.GET, "/api/v1/angsuran/**")
                            .hasAnyRole("CUSTOMER", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/angsuran/generate")
                            .hasAnyRole("BACKOFFICE", "SUPERADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/angsuran/bayar")
                        .hasAnyRole("CUSTOMER")

                        // ===== DOKUMEN  =====
                        .requestMatchers(HttpMethod.GET, "/api/v1/document/**")
                            .hasAnyRole("SUPERADMIN","MARKETING", "BRANCH_MANAGER", "BACKOFFICE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/document")
                            .hasAnyRole("SUPERADMIN","CUSTOMER")


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
                        .authenticationEntryPoint(unauthorizedEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
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
        konfigurasi.setAllowedHeaders(List.of("*"));
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