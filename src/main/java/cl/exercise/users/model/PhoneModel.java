package cl.exercise.users.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "PHONE_LIST")
public class PhoneModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String phoneNumber;
    private String codCity;
    private String codCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

}
