package org.override.models;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.override.core.models.HyperBody;

import javax.persistence.*;

@Entity
@Table(name = "user_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class UserModel implements HyperBody {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    @NonNull
    public String email;

    @NonNull
    public String name;

    @NonNull
    private String password;

    @NonNull
    @Lob
    private String publicKey;

    @NonNull
    @Lob
    private String secrectKey;

    public UserModel(@NotNull String email, @NotNull String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}
