package co.edu.uniquindio.application.dto;

public record EmailDTO(
        String subject,
        String body,
        String recipient
) {
}
