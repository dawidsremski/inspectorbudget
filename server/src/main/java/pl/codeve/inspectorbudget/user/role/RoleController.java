package pl.codeve.inspectorbudget.user.role;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/role")
@AllArgsConstructor
public class RoleController {

    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getRoles() {

        return new ResponseEntity<>(roleService.findAllRoleNames(), HttpStatus.OK);
    }
}
