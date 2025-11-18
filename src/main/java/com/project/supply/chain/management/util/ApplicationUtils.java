package com.project.supply.chain.management.util;

import com.project.supply.chain.management.Repositories.UserRepository;
import com.project.supply.chain.management.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ApplicationUtils {
    private final UserRepository userRepository;

    public String getLoggedInUserEmail()
    {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getUser(String email)
    {
        User user= userRepository.findByEmail(email);
        return user;
    }
    // User user = userRepository.findByEmail(email);
    //
    //        if (user == null) throw new RuntimeException("User not found");
}


