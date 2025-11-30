package co.edu.uniquindio.application.security;

import co.edu.uniquindio.application.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String message;
        int status;

        if (authException instanceof org.springframework.security.access.AccessDeniedException) {
            message = "No tienes los permisos necesarios para acceder a este recurso";
            status = 403; // Forbidden
        } else {
            message = "No tienes permisos para acceder a este recurso. Por favor, inicia sesión.";
            status = 401; // Unauthorized
        }

        ResponseDTO<String> dto = new ResponseDTO<>(true, message);
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
