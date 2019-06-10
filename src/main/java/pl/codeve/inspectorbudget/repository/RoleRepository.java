package pl.codeve.inspectorbudget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.codeve.inspectorbudget.model.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
