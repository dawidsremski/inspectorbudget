package pl.codeve.inspectorbudget.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.codeve.inspectorbudget.common.ApiResponse;
import pl.codeve.inspectorbudget.security.CurrentUser;
import pl.codeve.inspectorbudget.security.UserPrincipal;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "role", required = false) List<String> roleNames,
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {

        return new ResponseEntity<>(userService
                .findAll(id, name, username, email, roleNames, pageable), HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal currentUser) {

        return new ResponseEntity<>(userService.getCurrentUser(currentUser), HttpStatus.OK);
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> editCurrentUser(@CurrentUser UserPrincipal userPrincipal,
                                             @Valid @RequestBody EditProfileRequest editProfileRequest) {

        userService.editCurrentUser(userPrincipal, editProfileRequest);

        return new ResponseEntity<>(new ApiResponse(true, "Changes successfully saved."), HttpStatus.OK);
    }

    @GetMapping("/checkUsernameAvailability")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam(name = "username") String username) {

        return new ResponseEntity<>(new UserAvailability(
                !userService.existsByUserName(username)
        ), HttpStatus.OK);
    }

    @GetMapping("/checkEmailAvailability")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam(name = "email") String email) {

        return new ResponseEntity<>(new UserAvailability(
                !userService.existsByEmail(email)
        ), HttpStatus.OK);
    }
}

