package co.edu.uniquindio.application.models.enums;

public enum MessageType {
    CHAT,           // Mensaje normal de chat
    JOIN,           // Usuario se unió al chat
    LEAVE,          // Usuario salió del chat
    TYPING,         // Usuario está escribiendo...
    STOP_TYPING,    // Usuario dejó de escribir
    READ_RECEIPT,   // Confirmación de lectura
    DELIVERY        // Confirmación de entrega
}
