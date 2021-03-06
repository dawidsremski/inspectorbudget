package pl.codeve.inspectorbudget.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserNameOrEmail(String usernameOrEmail, String usernameOrEmail1);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);

    Page<User> findAll(Specification<User> userSpec, Pageable pageable);
}
