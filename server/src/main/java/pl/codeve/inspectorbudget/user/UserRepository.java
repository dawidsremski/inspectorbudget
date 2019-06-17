package pl.codeve.inspectorbudget.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserNameOrEmail(String usernameOrEmail, String usernameOrEmail1);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);
}
