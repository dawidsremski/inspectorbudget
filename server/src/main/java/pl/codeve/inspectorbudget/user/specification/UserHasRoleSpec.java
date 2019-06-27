package pl.codeve.inspectorbudget.user.specification;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import pl.codeve.inspectorbudget.user.User;
import pl.codeve.inspectorbudget.user.User_;
import pl.codeve.inspectorbudget.user.role.Role;
import pl.codeve.inspectorbudget.user.role.RoleName;
import pl.codeve.inspectorbudget.user.role.RoleRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Data
public class UserHasRoleSpec implements Specification<User> {

    private RoleRepository roleRepository;
    private String roleName;

    public UserHasRoleSpec(String roleName, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.roleName = roleName;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        Role role;

        try {
            role = roleRepository.findByName(RoleName.valueOf(roleName)).orElse(null);
        } catch (IllegalArgumentException e) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(false));
        }

        if (role == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }
        return criteriaBuilder.isMember(role, root.get(User_.ROLES));
    }
}
