package com.barbershop.config;

import com.barbershop.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOriginPatterns(List.of(
                frontendUrl,
                "https://barbershop-frontend-steel.vercel.app",
                "https://*.vercel.app",
                "http://localhost:3000",
                "http://localhost:5173"
        ));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setExposedHeaders(List.of("Authorization"));
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // rutas públicas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/barbers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()

                        // availability público
                        .requestMatchers(HttpMethod.GET, "/api/appointments/availability").permitAll()

                        // Swagger público
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // solo BARBER_ADMIN
                        .requestMatchers(HttpMethod.POST,   "/api/barbers/**").hasRole("BARBER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/barbers/**").hasRole("BARBER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/barbers/**").hasRole("BARBER_ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/services/**").hasRole("BARBER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/services/**").hasRole("BARBER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/services/**").hasRole("BARBER_ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("BARBER_ADMIN")
                        .requestMatchers("/api/users/**").hasRole("BARBER_ADMIN")

                        // BARBER: puede ver citas y marcar completadas
                        .requestMatchers(HttpMethod.GET,  "/api/appointments/barber/**").hasAnyRole("BARBER_ADMIN", "BARBER")
                        .requestMatchers(HttpMethod.POST, "/api/appointments/*/complete").hasAnyRole("BARBER_ADMIN", "BARBER")

                        // cualquier otra requiere autenticación
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}