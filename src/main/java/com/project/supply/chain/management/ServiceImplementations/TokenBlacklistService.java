package com.project.supply.chain.management.ServiceImplementations;

import com.project.supply.chain.management.Repositories.BlacklistedTokenRepository;
import com.project.supply.chain.management.Repositories.BlacklistedTokenRepository;
import com.project.supply.chain.management.entity.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository tokenBlacklistRepository;

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    public void blacklistToken(String token) {
        if (!tokenBlacklistRepository.existsByToken(token)) {
         BlacklistedToken entry =
                    new com.project.supply.chain.management.entity.BlacklistedToken();
            entry.setToken(token);
            tokenBlacklistRepository.save(entry);
        }
    }
}
