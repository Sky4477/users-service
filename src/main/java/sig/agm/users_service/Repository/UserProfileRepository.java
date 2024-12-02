package sig.agm.users_service.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sig.agm.users_service.Model.UserProfile;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

}