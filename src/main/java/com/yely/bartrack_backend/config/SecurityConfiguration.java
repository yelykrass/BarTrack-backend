package com.yely.bartrack_backend.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.yely.bartrack_backend.security.JpaUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
        @Value("${api-endpoint}")
        String endpoint;

        private final JpaUserDetailsService jpaUserDetailsService;

        public SecurityConfiguration(JpaUserDetailsService jpaUserDetailsService) {
                this.jpaUserDetailsService = jpaUserDetailsService;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfiguration()))
                                .csrf(csrf -> csrf.disable())
                                .headers(header -> header
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                                .formLogin(form -> form.disable())
                                .logout(out -> out
                                                .logoutUrl(endpoint + "/logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers("/public").permitAll()

                                                .requestMatchers(HttpMethod.POST, endpoint + "/register")
                                                .hasRole("ADMIN")
                                                .requestMatchers(endpoint + "/login").hasAnyRole("ADMIN", "USER")
                                                .requestMatchers(HttpMethod.GET, endpoint + "/private").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .userDetailsService(jpaUserDetailsService)
                                .httpBasic(withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

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
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
                configuration.setExposedHeaders(Arrays.asList("Authorization"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
