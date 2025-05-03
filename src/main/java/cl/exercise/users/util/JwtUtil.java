package cl.exercise.users.util;

import cl.exercise.users.model.UserModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key jwtSecretKey;

    @Value("${jwt.expiration.months}")
    private int expirationMonths;

    public JwtUtil(Key jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    public String generateToken(UserModel user) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);

        LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(expirationMonths);
        Date expiration = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
