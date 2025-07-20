package com.db.dsg.config;

import com.db.dsg.filter.JWTAuthenticationFilter;
import com.db.dsg.service.impl.MemberUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtFilter;
    private final MemberUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // parameters: saltLength, hashLength, parallelism, memory, iterations
        return new Argon2PasswordEncoder(16, 32, 1, 1 << 13, 3);
    }

    // ✅ Authentication Provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ✅ Authentication Manager Bean (required in Spring Security 6)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ✅ Security Filter Chain (Spring Security 6 compatible)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers(
                                "/v3/api-docs/**",                          // ✅ OpenAPI JSON
                                "/swagger-ui/**",                           // ✅ Swagger static resources
                                "/swagger-ui.html"                            // ✅ Swagger index
                        ).permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/actuator/**", "/health").permitAll()

                        // Everything else needs authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowedMethods("*");
//            }
//        };
//    }
}