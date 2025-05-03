package cl.exercise.users.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "USERS")
public class UserModel {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwd;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private LocalDateTime lastLogin;
    private Boolean isActive;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PhoneModel> phoneList;
    @Column(length = 5000)
    private String token;

}
