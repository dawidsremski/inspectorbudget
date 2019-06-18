package pl.codeve.inspectorbudget.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.codeve.inspectorbudget.common.ApiResponse;
import pl.codeve.inspectorbudget.security.JwtAuthenticationResponse;
import pl.codeve.inspectorbudget.security.LoginRequest;
import pl.codeve.inspectorbudget.security.SignUpRequest;
import pl.codeve.inspectorbudget.user.*;
import pl.codeve.inspectorbudget.user.role.Role;
import pl.codeve.inspectorbudget.user.role.RoleName;
import pl.codeve.inspectorbudget.user.role.RoleRepository;
import pl.codeve.inspectorbudget.user.UserRepository;
import pl.codeve.inspectorbudget.security.JwtTokenProvider;
import pl.codeve.inspectorbudget.storage.StorageService;
import pl.codeve.inspectorbudget.user.avatar.Avatar;
import pl.codeve.inspectorbudget.user.avatar.AvatarRepository;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/api/auth")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AvatarRepository avatarRepository;
    private StorageService storageService;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;

    AuthenticationController(AuthenticationManager authenticationManager,
                             UserRepository userRepository,
                             RoleRepository roleRepository,
                             AvatarRepository avatarRepository,
                             StorageService storageService,
                             PasswordEncoder passwordEncoder,
                             JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.avatarRepository = avatarRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws Exception {

        String reCAPTCHAResponse = signUpRequest.getReCAPTCHAResponse();
        //TODO Make POST request to https://www.google.com/recaptcha/api/siteverify and verify reCAPTCHA
        Boolean reCAPTCHAVerificationResult = true;
        if (!reCAPTCHAVerificationResult) {
            return new ResponseEntity<>(new ApiResponse(false, "reCAPTCHA verification failed!"),
                    HttpStatus.UNAUTHORIZED);
        }

        if (userRepository.existsByUserName(signUpRequest.getUserName())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getName(), signUpRequest.getUserName(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        if (signUpRequest.getAvatarId() != null) {
            Optional<Avatar> avatarOptional = this.avatarRepository.findById(signUpRequest.getAvatarId());
            if (avatarOptional.isPresent()) {
                Avatar avatar = avatarOptional.get();
                user.setAvatar(avatar);
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new Exception("user Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUserName()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "user registered successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
