package com.yely.bartrack_backend.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.yely.bartrack_backend.security.JpaUserDetailsService;
import com.yely.bartrack_backend.security.JwtAuthenticationFilter;
import com.yely.bartrack_backend.security.JwtUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor

public class SecurityConfiguration {

        private final JpaUserDetailsService jpaUserDetailsService;
        private final JwtUtils jwtUtils;

        @Value("${api-endpoint}")
        String endpoint;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtils, jpaUserDetailsService);

                http
                                .cors(cors -> cors.configurationSource(corsConfiguration()))
                                .csrf(csrf -> csrf.disable())
                                .headers(header -> header
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                                .formLogin(form -> form.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // .requestMatchers("/api/v1/check-session").permitAll()
                                                .requestMatchers(endpoint + "/auth/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()

                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                // .requestMatchers("/public").permitAll()

                                                // .requestMatchers(HttpMethod.POST, endpoint + "/register")
                                                // .hasRole("ADMIN")
                                                // .requestMatchers(endpoint + "/login").hasAnyRole("ADMIN", "USER")
                                                // .requestMatchers(HttpMethod.GET, endpoint +
                                                // "/private").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtFilter,
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                                .userDetailsService(jpaUserDetailsService)
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((req, res, ex2) -> res
                                                                .sendError(HttpServletResponse.SC_UNAUTHORIZED)));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        CorsConfigurationSource corsConfiguration() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowCredentials(true);
                configuration.setAllowedOrigins(Arrays.asList("https://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }
}
