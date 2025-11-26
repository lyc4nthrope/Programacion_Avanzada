package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.EmailDTO;

public interface EmailService {
    void sendMail(EmailDTO emailDTO) throws Exception;
}