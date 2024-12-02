package sig.agm.users_service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_profile")
@AllArgsConstructor @NoArgsConstructor
public class UserProfile {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "first_name", length = 75)
    private String first_name;

    @Column(name = "last_name", length = 75)
    private String last_name;

    @Column(name = "phone_number", length = 10)
    private String phone_number;

    @Column(name = "adresse", length = 80)
    private String adresse;

    @Column(name = "city", length = 80)
    private String city;
    @Column(name = "country", length = 80)
    private String country;
    @Column(name = "postal_code", length = 10)
    private String postal_code;

    public UserProfile(String first_name, String last_name, String phone_number, String adresse, String city, String postal_code) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.adresse = adresse;
        this.city = city;
        this.postal_code = postal_code;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.country="GUINEE";
        if(this.id ==null){
            this.id = UUID.randomUUID();
        }
    }
}