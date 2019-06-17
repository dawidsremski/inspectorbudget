package pl.codeve.inspectorbudget.user.avatar;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class AvatarResponse {
    Long avatarId;
    String avatarUrl;
}
