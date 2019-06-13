package pl.codeve.inspectorbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.codeve.inspectorbudget.model.Role;
import pl.codeve.inspectorbudget.model.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleName roleUser);
}
