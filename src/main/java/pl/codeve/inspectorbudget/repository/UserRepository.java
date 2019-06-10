package pl.codeve.inspectorbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.codeve.inspectorbudget.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
}
