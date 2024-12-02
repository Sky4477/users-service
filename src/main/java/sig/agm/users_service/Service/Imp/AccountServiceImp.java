package sig.agm.users_service.Service.Imp;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import sig.agm.users_service.Model.AppUser;
import sig.agm.users_service.Model.UserProfile;
import sig.agm.users_service.Model.Utils.AppRoles;
import sig.agm.users_service.Model.Utils.AppUserNotFoundException;
import sig.agm.users_service.Model.Utils.JwtUtils;
import sig.agm.users_service.Repository.AppUserRepository;
import sig.agm.users_service.Service.Interface.AccoutService;

@Service
@Transactional
@AllArgsConstructor

public class AccountServiceImp implements AccoutService {
    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public List<AppUser> findAllUsers() {
        return repo.findAll();
    }

    @Override
    public AppUser findUser(String userName) {
        return repo.findByUsername(userName);
    }

    @Override
    public AppUser findUserByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public AppUser findUserFromToken(String userToken) {
        if (userToken != null && !userToken.isEmpty()) {
            String username = jwtUtils.getUserNameFromJwtToken(userToken);
            return repo.findByUsername(username);
        }
        throw new AppUserNotFoundException("Utilisateur non trouvé avec le token: " + userToken);
    }

    @Override
    public AppUser updateAppUser(AppUser appUser) {
        AppUser user = repo.findByUsername(appUser.getUsername());
        user.setPassword(passwordEncoder.encode(appUser.getPassword())); // Hachage du nouveau mot de passe
        user.setEmail(appUser.getEmail());
        user.setConnected(appUser.isConnected());
        user.setAppRoles(appUser.getAppRoles());
        return repo.save(user);
    }

    @Override
    public AppUser updateUserProfile(String username, UserProfile userProfile) {
        AppUser user = repo.findByUsername(username);
        if (user != null) {
            user.getUserProfile().setLast_name(userProfile.getLast_name());
            user.getUserProfile().setFirst_name(userProfile.getFirst_name());
            user.getUserProfile().setPhone_number(userProfile.getPhone_number());
            user.getUserProfile().setAdresse(userProfile.getAdresse());
            return repo.save(user);
        }
        throw new AppUserNotFoundException("User " + username + " not found");
    }

    @Override
    public String deleteUser(String userName) {
        if (repo.findByUsername(userName) != null) {
            repo.deleteByUsername(userName);
            return "User " + userName + " deleted";
        } else {
            throw new AppUserNotFoundException("User " + userName + " not found");
        }
    }

    @Override
    public String connexion(String email, String password) {
        AppUser user = repo.findByEmail(email);
        if (user == null) {
            throw new AppUserNotFoundException("Utilisateur non trouvé avec le email: " + email);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Mot de passe incorrect");
        }
        // Générer le token JWT
        return jwtUtils.generateJwtToken(user);
    }

    @Override
    public AppUser createAccount(AppUser appUser) {
        if (repo.findByUsername(appUser.getEmail()) != null) {
            throw new AppUserNotFoundException("User " + appUser.getEmail() + " already exist");
        } else if (repo.findByUsername(appUser.getUsername()) != null) {
            throw new AppUserNotFoundException("User " + appUser.getUsername() + " already exist");
        }
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setAppRoles(Collections.singleton(AppRoles.ROLES_USER));
        appUser.setUserProfile(new UserProfile());
        return repo.save(appUser);

    }

    @Override
    public AppUser updatePassword(String email, String password, String comfirmePassword) {
        AppUser appUser = repo.findByEmail(email);
        if (appUser != null && password.equals(comfirmePassword)) {
            appUser.setPassword(passwordEncoder.encode(password));
            return repo.save(appUser);
        }
        throw new AppUserNotFoundException("Utilisateur non trouvé avec le  e-mail " + email);
    }

    @Override
    public AppUser addRolesToUser(String username, String rolesname) {
        AppUser user = repo.findByUsername(username);
        if (user != null) {
            user.getAppRoles().add(AppRoles.valueOf(rolesname));
            return repo.save(user);
        }
        throw new AppUserNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur: " + username);
    }

    @Override
    public AppUser removeRolesToUser(String username, String rolesname) {
        AppUser user = repo.findByUsername(username);
        if (user != null) {
            user.getAppRoles().remove(AppRoles.valueOf(rolesname));
            return repo.save(user);
        }
        throw new AppUserNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur: " + username);
    }




    @Override
    public AppUser makeUserToRoots(String username) {
        AppUser user = repo.findByUsername(username);
        if (user != null) {
            user.getAppRoles().add(AppRoles.ROLES_SUPERUSER);
            user.getAppRoles().add(AppRoles.ROLES_ADMIN);
            user.getAppRoles().add(AppRoles.ROLES_PROPRIO);
            user.getAppRoles().add(AppRoles.ROLES_COMMERCANT);
            user.getAppRoles().add(AppRoles.ROLES_MAINTENANCIER);
            return repo.save(user);
        }
        throw new AppUserNotFoundException("User " + username + " not found");
    }

    @Override
    public AppUser CheckUserbyRoot(String email) {
        AppUser user = repo.findByEmail(email);
        if (user != null) {
            return user;
        }
        throw new AppUserNotFoundException("User " + email + " not found");
    }
    
}
