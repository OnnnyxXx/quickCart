package user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password; // Хранить в зашифрованном виде
    private String email;
    private String location;
    private int rating;
    private String role; // Роль пользователя (например, "ADMIN", "SELLER", "BUYER")

}
