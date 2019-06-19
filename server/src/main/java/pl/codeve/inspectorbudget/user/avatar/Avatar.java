package pl.codeve.inspectorbudget.user.avatar;

import lombok.*;
import pl.codeve.inspectorbudget.common.DateAudit;
import pl.codeve.inspectorbudget.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Avatar extends DateAudit {
    @Id
    @GeneratedValue
    @NonNull
    private Long id;
    @NotBlank
    @NonNull
    private String base64Avatar;
    @OneToMany
    @OneToOne(mappedBy = "avatar")
    private User user;
    boolean inUse = false;
}
