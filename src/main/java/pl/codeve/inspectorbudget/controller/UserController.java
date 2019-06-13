package pl.codeve.inspectorbudget.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.codeve.inspectorbudget.model.UserAvailability;
import pl.codeve.inspectorbudget.model.UserResponse;
import pl.codeve.inspectorbudget.repository.UserRepository;
import pl.codeve.inspectorbudget.security.CurrentUser;
import pl.codeve.inspectorbudget.security.UserPrincipal;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private UserRepository userRepository;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/checkUsernameAvailability")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam(name = "username") String username) {

        return new ResponseEntity<>(new UserAvailability(
                !userRepository.existsByUsername(username)
        ), HttpStatus.OK);
    }

    @GetMapping("/checkEmailAvailability")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam(name = "email") String email) {

        return new ResponseEntity<>(new UserAvailability(
                !userRepository.existsByEmail(email)
        ), HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new ResponseEntity<>(new UserResponse(currentUser.getId(), currentUser.getName(), currentUser.getUsername(), currentUser.getEmail()),
        HttpStatus.OK);
    }
}
