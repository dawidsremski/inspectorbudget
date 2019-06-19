package pl.codeve.inspectorbudget.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pl.codeve.inspectorbudget.security.recaptcha.ReCAPTCHAVerificationResponse;
import pl.codeve.inspectorbudget.security.recaptcha.ReCAPTCHAVerificationService;
import pl.codeve.inspectorbudget.user.*;
import pl.codeve.inspectorbudget.user.role.Role;
import pl.codeve.inspectorbudget.user.role.RoleName;
import pl.codeve.inspectorbudget.user.role.RoleRepository;
import pl.codeve.inspectorbudget.user.UserRepository;
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
    private ReCAPTCHAVerificationService reCAPTCHAVerificationService;

    private Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    AuthenticationController(AuthenticationManager authenticationManager,
                             UserRepository userRepository,
                             RoleRepository roleRepository,
                             AvatarRepository avatarRepository,
                             StorageService storageService,
                             PasswordEncoder passwordEncoder,
                             JwtTokenProvider tokenProvider,
                             ReCAPTCHAVerificationService reCAPTCHAVerificationService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.avatarRepository = avatarRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.reCAPTCHAVerificationService = reCAPTCHAVerificationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws Exception {

        String reCAPTCHAResponse = signUpRequest.getReCAPTCHAResponse();
        ResponseEntity<ReCAPTCHAVerificationResponse> reCAPTCHAVerificationEntity = reCAPTCHAVerificationService.verify(reCAPTCHAResponse);

        if (reCAPTCHAVerificationEntity.getStatusCode().is2xxSuccessful()) {
            if (!reCAPTCHAVerificationEntity.getBody().getSuccess()) {
                return new ResponseEntity<>(new ApiResponse(false, "reCAPTCHA verification failed! You're unauthorized."),
                        HttpStatus.UNAUTHORIZED);
            }
        } else {
            logger.error("reCAPTCHA verification failed, due to network error with status code "
                    + reCAPTCHAVerificationEntity.getStatusCodeValue() + "."
                    + "Verification skipped when registering user with username: "
                    + signUpRequest.getUserName() + "!");
        }

        if (!signUpRequest.getPassword().equals(signUpRequest.getRepeatedPassword())) {
            return new ResponseEntity<>(new ApiResponse(false, "The passwords you entered don\'t match!"),
                    HttpStatus.BAD_REQUEST);
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

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new Exception("user Role not set."));

        user.setRoles(Collections.singleton(userRole));

        Avatar avatar = null;

        if (signUpRequest.getAvatarId() != null) {
            Optional<Avatar> avatarOptional = this.avatarRepository.findById(signUpRequest.getAvatarId());
            if (avatarOptional.isPresent()) {
                avatar = avatarOptional.get();
                user.setAvatar(avatar);
            }
        }

        User result = userRepository.save(user);

        if (avatar != null) {
            avatar.setInUse(true);
            avatarRepository.save(avatar);
        }

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