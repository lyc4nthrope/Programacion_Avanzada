package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.User;
import co.edu.uniquindio.application.models.enums.Role;
import co.edu.uniquindio.application.models.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    List<User> findByStatus(UserStatus status);
    List<User> findByStatusEquals(UserStatus status);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    Long countByStatus(UserStatus status);
}