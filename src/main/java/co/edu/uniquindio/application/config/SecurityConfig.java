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
                        // ================================================
                        // RUTAS PÚBLICAS (sin autenticación)
                        // ================================================

                        // Autenticación y registro
                        .requestMatchers("/api/auth/**").permitAll()

                        // Recuperación de contraseña (CRÍTICO: debe ser público)
                        .requestMatchers("/api/password-reset/**").permitAll()

                        // Documentación API
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoints de prueba
                        .requestMatchers("/api/greeting/**").permitAll()

                        // ================================================
                        // CONSULTAS PÚBLICAS (solo lectura, sin autenticación)
                        // ================================================

                        // Ver alojamientos (solo GET)
                        .requestMatchers(HttpMethod.GET, "/api/accommodations/**").permitAll()

                        // Ver reseñas de alojamientos (solo GET)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/accommodation/**").permitAll()

                        // Ver fotos de alojamientos (solo GET)
                        .requestMatchers(HttpMethod.GET, "/api/accommodation-photos/**").permitAll()

                        // Ver perfiles de anfitrión (solo GET)
                        .requestMatchers(HttpMethod.GET, "/api/host-profiles/**").permitAll()

                        // Ver disponibilidad (solo GET)
                        .requestMatchers(HttpMethod.GET, "/api/availability/**").permitAll()

                        // ================================================
                        // SOLO GUEST (usuarios normales)
                        // ================================================

                        // Crear reservas
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasAuthority("GUEST")

                        // Cancelar sus propias reservas
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/cancel").hasAuthority("GUEST")

                        // Agregar favoritos
                        .requestMatchers(HttpMethod.POST, "/api/favorites").hasAuthority("GUEST")
                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/**").hasAuthority("GUEST")

                        // Crear reseñas (GUEST o HOST)
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasAnyAuthority("GUEST", "HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").hasAnyAuthority("GUEST", "HOST")
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyAuthority("GUEST", "HOST")

                        // ================================================
                        // SOLO HOST (anfitriones)
                        // ================================================

                        // Gestionar alojamientos
                        .requestMatchers(HttpMethod.POST, "/api/accommodations").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/accommodations/**").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.DELETE, "/api/accommodations/**").hasAuthority("HOST")

                        // Gestionar fotos de alojamientos
                        .requestMatchers(HttpMethod.POST, "/api/accommodation-photos/**").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.DELETE, "/api/accommodation-photos/**").hasAuthority("HOST")

                        // Gestionar perfil de anfitrión
                        .requestMatchers(HttpMethod.POST, "/api/host-profiles").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/host-profiles/**").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.DELETE, "/api/host-profiles/**").hasAuthority("HOST")

                        // Confirmar reservas
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/confirm").hasAuthority("HOST")

                        // Gestionar disponibilidad
                        .requestMatchers(HttpMethod.POST, "/api/availability/**").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/availability/**").hasAuthority("HOST")
                        .requestMatchers(HttpMethod.DELETE, "/api/availability/**").hasAuthority("HOST")

                        // ================================================
                        // SOLO ADMIN (administradores)
                        // ================================================

                        // Gestión total de usuarios
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/activate").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/deactivate").hasAuthority("ADMIN")

                        // ================================================
                        // GUEST, HOST o ADMIN (usuarios autenticados)
                        // ================================================

                        // Ver y editar su propio perfil
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAnyAuthority("GUEST", "HOST", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAnyAuthority("GUEST", "HOST", "ADMIN")

                        // Gestión de mensajes y chats
                        .requestMatchers("/api/messages/**").authenticated()
                        .requestMatchers("/api/chats/**").authenticated()

                        // Ver sus propias reservas
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").authenticated()

                        // Gestión de pagos
                        .requestMatchers("/api/payments/**").authenticated()

                        // Subir imágenes
                        .requestMatchers(HttpMethod.POST, "/api/images").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/images").authenticated()

                        // ================================================
                        // Cualquier otra ruta requiere autenticación
                        // ================================================
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