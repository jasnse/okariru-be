package com.project.binar.okariru.service.jwtAuth;

import com.project.binar.okariru.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "0123456789-0123456789-0123456789-0123456789";

    private JwtService service;
    private AppUser user;

    @BeforeEach
    void setUp() {
        service = new JwtService(SECRET, 60);
        user = new AppUser("budi", "HASH", "ADMIN", null);
    }

    @Test
    void issueDanParse_claimBerisiSubjectRoleDanExpiry() {
        Instant now = Instant.now();

        Claims claims = service.parse(service.issue(user, now));

        assertEquals("budi", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertNotNull(claims.getExpiration());
        long ttlDetik = Duration.between(claims.getIssuedAt().toInstant(), claims.getExpiration().toInstant()).getSeconds();
        assertEquals(60 * 60, ttlDetik);
    }

    @Test
    void issueWithoutExpiry_tidakPunyaExpiration() {
        Claims claims = service.parse(service.issueWithoutExpiry(user, Instant.now()));

        assertEquals("budi", claims.getSubject());
        assertNull(claims.getExpiration());
    }

    @Test
    void parse_tokenKadaluarsa() {
        String token = service.issue(user, Instant.now().minus(Duration.ofHours(2)));

        assertThrows(ExpiredJwtException.class, () -> service.parse(token));
    }

    @Test
    void parse_tokenDisignDenganKeyLain() {
        JwtService lain = new JwtService("ZZZZZZZZZZ-ZZZZZZZZZZ-ZZZZZZZZZZ-ZZZZZZZZZZ", 60);
        String token = lain.issue(user, Instant.now());

        assertThrows(JwtException.class, () -> service.parse(token));
    }

    @Test
    void parse_tokenBukanJwt() {
        assertThrows(JwtException.class, () -> service.parse("bukan-jwt"));
    }

    @Test
    void issue_userTanpaRole_claimRoleNull() {
        AppUser tanpaRole = new AppUser("budi", "HASH", null, null);

        Claims claims = service.parse(service.issue(tanpaRole, Instant.now()));

        assertNull(claims.get("role"));
    }
}
