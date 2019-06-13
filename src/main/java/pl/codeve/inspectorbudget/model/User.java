package pl.codeve.inspectorbudget.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends DateAudit {

    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @NaturalId
    @Column(unique = true)
    @Size(max = 20)
    private String username;

    @NotBlank
    @Email
    @NaturalId
    @Column(unique = true)
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 255)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
