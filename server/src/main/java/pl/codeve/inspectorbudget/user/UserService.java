package pl.codeve.inspectorbudget.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import pl.codeve.inspectorbudget.security.UserPrincipal;
import pl.codeve.inspectorbudget.user.avatar.Avatar;
import pl.codeve.inspectorbudget.user.avatar.AvatarRepository;
import pl.codeve.inspectorbudget.user.role.RoleRepository;
import pl.codeve.inspectorbudget.user.specification.UserFieldStartsWithSpec;
import pl.codeve.inspectorbudget.user.specification.UserHasIdSpec;
import pl.codeve.inspectorbudget.user.specification.UserHasRoleSpec;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private AvatarRepository avatarRepository;
    private RoleRepository roleRepository;

    Page<UserResponse> findAll(Long id, String name, String userName,
                               String email, List<String> roleNames, Pageable pageable) {

        Specification<User> userSpec = Specification
                .where(new UserHasIdSpec(id))
                .and(new UserFieldStartsWithSpec(User_.NAME, name))
                .and(new UserFieldStartsWithSpec(User_.USER_NAME, userName))
                .and(new UserFieldStartsWithSpec(User_.EMAIL, email));

        if (roleNames != null) {
            for (String roleName : roleNames)
                userSpec = userSpec.and(new UserHasRoleSpec(roleName, roleRepository));
        }

        return userRepository.findAll(userSpec, pageable).map(UserResponse::map);
    }

    UserResponse getCurrentUser(UserPrincipal userPrincipal) {

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return new UserResponse(
                userPrincipal.getId(),
                userPrincipal.getName(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles,
                (userPrincipal.getAvatar() != null) ? userPrincipal.getAvatar().getId() : null);
    }

    boolean existsByUserName(String username) {
        return userRepository.existsByUserName(username);
    }

    boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    void editCurrentUser(UserPrincipal userPrincipal, EditProfileRequest editProfileRequest) {

        User user = userRepository.getOne(userPrincipal.getId());
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
    }
}
