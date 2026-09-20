package com.project.binar.okariru.filter;

import com.project.binar.okariru.entity.AppUser;
import com.project.binar.okariru.exception.UnauthorizedHandler;
import com.project.binar.okariru.service.jwtAuth.AppUserDetailsService;
import com.project.binar.okariru.service.jwtAuth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private AppUserDetailsService userDetailsService;
    @Mock
    private UnauthorizedHandler unauthorizedHandler;
    @Mock
    private FilterChain chain;
    @Mock
    private Claims claims;

    @InjectMocks
    private JwtAuthFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tanpaHeaderAuthorization_langsungLanjutKeChain() throws Exception {
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, unauthorizedHandler);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void headerBukanBearer_langsungLanjutKeChain() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, unauthorizedHandler);
    }

    @Test
    void tokenValid_mengisiSecurityContextDanLanjutKeChain() throws Exception {
        AppUser user = new AppUser("budi", "HASH", "ADMIN", null);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token-valid");
        when(jwtService.parse("token-valid")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("budi");
        when(userDetailsService.loadUserByUsername("budi")).thenReturn(user);

        filter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(user, auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(chain).doFilter(request, response);
        verifyNoInteractions(unauthorizedHandler);
    }

    @Test
    void tokenTidakValid_jwtException_401DanChainTidakDipanggil() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token-rusak");
        when(jwtService.parse("token-rusak")).thenThrow(new JwtException("signature salah"));

        filter.doFilter(request, response, chain);

        verify(unauthorizedHandler).response(response, "Token tidak valid");
        verify(chain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void userDariTokenSudahTidakAda_401() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token-valid");
        when(jwtService.parse("token-valid")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("hantu");
        when(userDetailsService.loadUserByUsername("hantu")).thenThrow(new UsernameNotFoundException("tidak ada"));

        filter.doFilter(request, response, chain);

        verify(unauthorizedHandler).response(response, "Token tidak valid");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void tokenKosong_illegalArgument_401() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer ");
        when(jwtService.parse("")).thenThrow(new IllegalArgumentException("token kosong"));

        filter.doFilter(request, response, chain);

        verify(unauthorizedHandler).response(response, "Token tidak valid");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void tokenGagal_membersihkanAuthenticationLamaDiSecurityContext() throws Exception {
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("sisa-login-lama", null, List.of())));
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token-rusak");
        when(jwtService.parse("token-rusak")).thenThrow(new JwtException("expired"));

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
