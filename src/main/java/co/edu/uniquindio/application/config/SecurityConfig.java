package co.edu.uniquindio.application.config;

import co.edu.uniquindio.application.security.CustomAccessDeniedHandler;
import co.edu.uniquindio.application.security.JwtAuthenticationEntryPoint;
import co.edu.uniquindio.application.security.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        // RUTAS PÚBLICAS (sin autenticación)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/greeting/**").permitAll()

                        // CONSULTAS PÚBLICAS (solo lectura, sin autenticación)
                        .requestMatchers(HttpMethod.GET, "/api/accommodations/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/accommodation/**").permitAll()

                        // SOLO GUEST (usuarios normales)
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasAuthority("GUEST")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/cancel").hasAuthority("GUEST")
                        .requestMatchers(HttpMethod.POST, "/api/favorites").hasAuthority("GUEST")
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasAnyAuthority("GUEST", "HOST")

                        // SOLO HOST (anfitriones)
                        .requestMatchers(HttpMethod.POST, "/api/accommodations").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/accommodations/*").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.DELETE, "/api/accommodations/*").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.POST, "/api/host-profiles").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/confirm").hasAuthority("HOST")

                        // SOLO ADMIN (administradores)
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/activate").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/deactivate").hasAuthority("ADMIN")

                        // GUEST o ADMIN (usuarios normales o administradores)
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAnyAuthority("GUEST", "HOST", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAnyAuthority("GUEST", "HOST", "ADMIN")

                        // Cualquier usuario autenticado
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint());
                    ex.accessDeniedHandler(accessDeniedHandler);
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}