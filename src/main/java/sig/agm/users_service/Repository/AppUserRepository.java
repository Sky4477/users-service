package sig.agm.users_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sig.agm.users_service.Model.AppUser;

import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    AppUser findByUsername(String username);
    AppUser findByEmail(String email);
    AppUser findByUsernameOrEmail(String usernameOrEmail, String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    AppUser findByUsernameAndPassword(String username, String password);

    boolean deleteByUsername(String userName);
}