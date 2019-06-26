package pl.codeve.inspectorbudget.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.codeve.inspectorbudget.common.ApiResponse;
import pl.codeve.inspectorbudget.security.CurrentUser;
import pl.codeve.inspectorbudget.security.UserPrincipal;
import pl.codeve.inspectorbudget.user.avatar.Avatar;
import pl.codeve.inspectorbudget.user.avatar.AvatarRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private UserRepository userRepository;
    private AvatarRepository avatarRepository;

    private UserResponse map(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUserName(), user.getEmail(),
                user.getRoles().stream().map(role -> role.getName().toString()).collect(Collectors.toList()),
                (user.getAvatar().isPresent()) ? user.getAvatar().get().getId() : null);
    }

    UserController(UserRepository userRepository,
                   AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUsers(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        Page<UserResponse> userResponses = users.map(this::map);
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
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

