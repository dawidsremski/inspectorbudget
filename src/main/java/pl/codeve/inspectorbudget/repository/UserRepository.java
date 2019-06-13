package pl.codeve.inspectorbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.codeve.inspectorbudget.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsernameOrEmail(String usernameOrEmail, String usernameOrEmail1);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
