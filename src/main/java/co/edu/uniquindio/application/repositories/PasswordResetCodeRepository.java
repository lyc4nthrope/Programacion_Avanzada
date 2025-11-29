package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, String> {

    // Buscar código por valor
    Optional<PasswordResetCode> findByCode(String code);

    // Buscar código válido (no usado y no expirado)
    @Query("SELECT p FROM PasswordResetCode p WHERE p.code = :code AND p.used = false AND p.expiresAt > :now")
    Optional<PasswordResetCode> findValidCode(@Param("code") String code, @Param("now") LocalDateTime now);

    // Buscar códigos no usados de un usuario
    @Query("SELECT p FROM PasswordResetCode p WHERE p.user.id = :userId AND p.used = false")
    List<PasswordResetCode> findUnusedCodesByUser(@Param("userId") String userId);

    // Eliminar códigos expirados
    @Query("DELETE FROM PasswordResetCode p WHERE p.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
}