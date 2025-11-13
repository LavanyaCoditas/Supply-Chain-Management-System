package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.BlacklistedTokenRepository;
import com.project.supply.chain.management.dto.ApiResponseDto;
import com.project.supply.chain.management.entity.BlacklistedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthService(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    public ApiResponseDto<Void> logout(String token) {
        if (token == null || token.isBlank()) {
            return new ApiResponseDto<>(false, "No token provided", null);
        }

        String rawToken = token.replace("Bearer ", "").trim();

        try {

            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

            Claims claims = Jwts.parser()
                    .verifyWith(key)   // <-- replaces setSigningKey()
                    .build()           // <-- build() must be called before parsing
                    .parseSignedClaims(rawToken)
                    .getPayload();

            Date expiration = claims.getExpiration();

            if (!blacklistedTokenRepository.existsByToken(rawToken)) {
                blacklistedTokenRepository.save(new BlacklistedToken(
                        null,
                        rawToken,
                        expiration.toInstant()
                ));
            }

            SecurityContextHolder.clearContext();

            return new ApiResponseDto<>(true, "Logged out successfully", null);
        } catch (Exception e) {
            return new ApiResponseDto<>(false, "Invalid or expired token", null);
        }
    }
}
