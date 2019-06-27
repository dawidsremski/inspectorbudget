package pl.codeve.inspectorbudget.user.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {

    RoleRepository roleRepository;

    private String map(Role role) {
        return role.getName().toString();
    }

    List<String> findAllRoleNames() {
        return roleRepository.findAll().stream()
                .map(this::map).collect(Collectors.toList());
    }
}
