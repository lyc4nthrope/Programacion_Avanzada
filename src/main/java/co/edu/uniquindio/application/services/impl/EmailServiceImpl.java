package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.EmailDTO;
import co.edu.uniquindio.application.services.EmailService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${mail.host}")
    private String smtpHost;

    @Value("${mail.port}")
    private int smtpPort;

    @Value("${mail.username}")
    private String smtpUsername;

    @Value("${mail.password}")
    private String smtpPassword;

    @Override
    @Async
    public void sendMail(EmailDTO emailDTO) throws Exception {
        try {
            Email email = EmailBuilder.startingBlank()
                    .from(smtpUsername)
                    .to(emailDTO.recipient())
                    .withSubject(emailDTO.subject())
                    .withPlainText(emailDTO.body())
                    .buildEmail();

            try (Mailer mailer = MailerBuilder
                    .withSMTPServer(smtpHost, smtpPort, smtpUsername, smtpPassword)
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .withDebugLogging(true)
                    .buildMailer()) {

                mailer.sendMail(email);
                System.out.println("✅ Email enviado exitosamente a: " + emailDTO.recipient());
            }
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            throw e;
        }
    }
}