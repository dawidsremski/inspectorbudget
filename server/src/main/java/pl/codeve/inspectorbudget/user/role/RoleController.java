package pl.codeve.inspectorbudget.user.role;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/role")
public class RoleController {

    private RoleRepository roleRepository;

    private String map(Role role) {
        return role.getName().toString();
    }

    RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getRoles() {

        List<String> roles = roleRepository.findAll().stream()
                .map(this::map).collect(Collectors.toList());

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
