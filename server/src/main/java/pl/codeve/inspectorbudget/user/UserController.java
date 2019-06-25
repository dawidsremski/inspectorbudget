package pl.codeve.inspectorbudget.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.codeve.inspectorbudget.common.ApiResponse;
import pl.codeve.inspectorbudget.user.UserAvailability;
import pl.codeve.inspectorbudget.user.UserResponse;
import pl.codeve.inspectorbudget.user.UserRepository;
import pl.codeve.inspectorbudget.security.CurrentUser;
import pl.codeve.inspectorbudget.security.UserPrincipal;
import pl.codeve.inspectorbudget.user.avatar.Avatar;
import pl.codeve.inspectorbudget.user.avatar.AvatarRepository;
import pl.codeve.inspectorbudget.user.role.Role;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private UserRepository userRepository;
    private AvatarRepository avatarRepository;

    UserController(UserRepository userRepository,
                   AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream().map(u -> new UserResponse(u.getId(), u.getName(), u.getUserName(), u.getEmail(),
                        u.getRoles().stream().map(Role::toString).collect(Collectors.toList()),
                        u.getAvatar().isPresent()? u.getAvatar().get().getId() : null))
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/checkUsernameAvailability")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam(name = "username") String username) {

        return new ResponseEntity<>(new UserAvailability(
                !userRepository.existsByUserName(username)
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
        List<String> roles = currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        UserResponse userResponse = new UserResponse(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                roles,
                (currentUser.getAvatar() != null) ? currentUser.getAvatar().getId() : null);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> editUserProfile(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody EditProfileRequest editProfileRequest) {
        User user = userRepository.getOne(currentUser.getId());
        if (!editProfileRequest.getName().equals(user.getUserName()) && editProfileRequest.getName() != null) {
            user.setName(editProfileRequest.getName());
        }
        if (!editProfileRequest.getEmail().equals(user.getEmail()) && editProfileRequest.getEmail() != null) {
            user.setEmail(editProfileRequest.getEmail());
        }

        Avatar oldAvatar = null;

        if (editProfileRequest.getAvatarId() != null) {

            if (user.getAvatar().isPresent()) {
                oldAvatar = user.getAvatar().get();
                if (!oldAvatar.getId().equals(editProfileRequest.getAvatarId())) {
                    oldAvatar.setInUse(false);
                    avatarRepository.save(oldAvatar);
                }
            }
            if (oldAvatar == null || !oldAvatar.getId().equals(editProfileRequest.getAvatarId())) {
                Avatar avatar = avatarRepository.getOne(editProfileRequest.getAvatarId());
                user.setAvatar(avatar);
                avatar.setInUse(true);
                avatarRepository.save(avatar);
            }
        } else if (user.getAvatar().isPresent()) {
            oldAvatar = user.getAvatar().get();
            oldAvatar.setInUse(false);
            avatarRepository.save(oldAvatar);
            user.setAvatar(null);
        }

        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "Changes successfully saved."), HttpStatus.OK);
    }
}

