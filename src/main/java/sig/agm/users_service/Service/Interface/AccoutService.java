package sig.agm.users_service.Service.Interface;

import sig.agm.users_service.Model.AppUser;
import sig.agm.users_service.Model.UserProfile;

import java.util.List;

public interface AccoutService {
    List<AppUser> findAllUsers();
    AppUser findUser(String userName);
    AppUser findUserByEmail(String email);
    AppUser findUserFromToken(String userToken);
    AppUser updateAppUser(AppUser appUser);
    AppUser updateUserProfile(String username, UserProfile userProfile);
    String deleteUser(String userName);
    AppUser CheckUserbyRoot(String email);

    String connexion(String email, String password);

    AppUser createAccount(AppUser appUser);
    AppUser updatePassword(String email, String password,String comfirmePassword);
    AppUser addRolesToUser(String username, String rolesname);
    AppUser removeRolesToUser(String username, String rolesname);

    AppUser makeUserToRoots(String username);

}
