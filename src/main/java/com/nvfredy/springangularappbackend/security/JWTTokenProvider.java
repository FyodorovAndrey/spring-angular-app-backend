package com.nvfredy.springangularappbackend.security;

import com.nvfredy.springangularappbackend.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTTokenProvider {

    public static final Logger LOGGER = LoggerFactory.getLogger(JWTTokenProvider.class);


    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Date start = new Date(System.currentTimeMillis());
        Date expiryTime = new Date(start.getTime() + SecurityConstants.EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claimsMap = new HashMap<>();

        claimsMap.put("id", userId);
        claimsMap.put("username", user.getEmail());
        claimsMap.put("firstname", user.getFirstName());
        claimsMap.put("lastname", user.getLastName());

        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(start)
                .setExpiration(expiryTime)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException |
                ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException exception) {
            LOGGER.error(exception.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();

        String id = (String) claims.get("id");
        return Long.parseLong(id);
    }

}
