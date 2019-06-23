package pl.codeve.inspectorbudget.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NaturalId;
import pl.codeve.inspectorbudget.common.DateAudit;
import pl.codeve.inspectorbudget.user.avatar.Avatar;
import pl.codeve.inspectorbudget.user.role.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
public class User extends DateAudit {

    public User(String name, String userName, String email, String password, Avatar avatar) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Size(max = 50)
    @NonNull
    private String name;

    @NotBlank
    @NaturalId
    @Column(unique = true)
    @Size(max = 20)
    @NonNull
    private String userName;

    @NotBlank
    @Email
    @Column(unique = true)
    @Size(max = 50)
    @NonNull
    private String email;

    @NotBlank
    @Size(max = 255)
    @NonNull
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime lastLogin;
}
