package com.example.microservicio_usuarios.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generarToken(String username) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean tokenExpirado(String token) {
        try {
            return extraerExpiracion(token).before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public boolean validarToken(String token, String username) {
        try {
            final String usernameExtraido = extraerUsername(token);
            return usernameExtraido.equals(username) && !tokenExpirado(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
