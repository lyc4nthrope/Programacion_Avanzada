package co.edu.uniquindio.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling  // ✅ Habilitar tareas programadas (@Scheduled)
public class AsyncConfig implements AsyncConfigurer {
}