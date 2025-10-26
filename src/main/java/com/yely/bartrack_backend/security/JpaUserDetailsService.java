package com.yely.bartrack_backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.yely.bartrack_backend.user.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔹 [DEBUG] Запит на аутентифікацію користувача: " + username);

        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("✅ [DEBUG] Користувача знайдено: " + user.getUsername());
                    System.out.println("🔑 [DEBUG] Його роль(і): " + user.getRoles());
                    return new SecurityUser(user);
                })
                .orElseThrow(() -> {
                    System.out.println("❌ [DEBUG] Користувача не знайдено: " + username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        // .map(SecurityUser::new)
        // .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

}
