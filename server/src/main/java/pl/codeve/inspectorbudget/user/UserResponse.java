package pl.codeve.inspectorbudget.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
class UserResponse {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String userName;
    @NonNull
    private String email;
    @NonNull
    private List<String> roles;
    private Long avatarId;

    static UserResponse map(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUserName(), user.getEmail(),
                user.getRoles().stream().map(role -> role.getName().toString()).collect(Collectors.toList()),
                (user.getAvatar().isPresent()) ? user.getAvatar().get().getId() : null);
    }
}
