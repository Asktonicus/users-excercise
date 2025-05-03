package cl.exercise.users.util;

import cl.exercise.users.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Key secretKey;

    @BeforeEach
    public void setUp() {
        secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        jwtUtil = new JwtUtil(secretKey);

        injectExpirationMonths(jwtUtil, 6);
    }

    @Test
    public void testGenerateToken_containsExpectedClaims() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setName("Test User");

        String token = jwtUtil.generateToken(user);

        Claims claims = parseToken(token);

        assertEquals(user.getId().toString(), claims.getSubject());
        assertEquals("user@example.com", claims.get("email"));
        assertEquals("Test User", claims.get("name"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void injectExpirationMonths(JwtUtil jwtUtil, int months) {
        try {
            var field = JwtUtil.class.getDeclaredField("expirationMonths");
            field.setAccessible(true);
            field.set(jwtUtil, months);
        } catch (Exception e) {
            throw new RuntimeException("Error injecting expirationMonths", e);
        }
    }
}

