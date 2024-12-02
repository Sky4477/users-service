package sig.agm.users_service.Web.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sig.agm.users_service.Model.AppUser;
import sig.agm.users_service.Model.UserProfile;
import sig.agm.users_service.Model.Utils.AppRoles;
import sig.agm.users_service.Model.Utils.JwtUtils;
import sig.agm.users_service.Service.Imp.AccountServiceImp;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sig.agm.users_service.Service.Imp.UserDetailServiceImp;
import sig.agm.users_service.Service.TokenBlacklistService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthControlleur {
    private final AccountServiceImp accountService;
    private TokenBlacklistService tokenBlacklistService;


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLES_ADMIN')")
    public ResponseEntity<List<AppUser>> allAccess() {
        List<AppUser> users = accountService.findAllUsers();
        return ResponseEntity.ok(users);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/checkUserbyRoot")
    @PreAuthorize("hasRole('ROLES_ROOT')")
    public ResponseEntity<AppUser> existUserbyRoot(@RequestParam String email) {
        AppUser users = accountService.CheckUserbyRoot(email);
        return ResponseEntity.ok(users);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser user) {
        return ResponseEntity.ok(accountService.createAccount(user));
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        String jwt = accountService.connexion(authRequest.getEmail(), authRequest.getPassword());
        AppUser appUser = accountService.findUserByEmail(authRequest.getEmail());
        AuthResponse response = new AuthResponse(jwt, appUser);
        return ResponseEntity.ok(response);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Retirer "Bearer " du token
        tokenBlacklistService.revokeToken(jwt);
        return ResponseEntity.ok("Logout successful");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/addRoles")
    public ResponseEntity<AppUser> addRoles(@RequestBody RoleRequest roleRequest) {
        AppUser appUser = accountService.addRolesToUser(roleRequest.getUsername(), roleRequest.getRole());
        if (appUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.ok(appUser);
    }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/user")
    public ResponseEntity<AppUser> me() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser appUser = accountService.findUser(user.getUsername());
        if (appUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // ou une réponse appropriée
        }
        return ResponseEntity.ok(appUser);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/update/Password")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordRequest appUser) {
        AppUser appUser1 = accountService.updatePassword(appUser.getEmail(), appUser.getPassword(), appUser.getComfirmePassword());
        if (appUser1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok("Password updated successfully");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/update/AppUser")
    public ResponseEntity<AppUser> updateAppUser(@RequestBody AppUser appUser) {
        return ResponseEntity.ok(accountService.updateAppUser(appUser));
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/update/UserProfile")
    public ResponseEntity<AppUser> updateUserProfile(@RequestBody UserProfileFrom userProfileFrom) {
        return ResponseEntity.ok(accountService.updateUserProfile(userProfileFrom.getUsername(), userProfileFrom.getUserProfile()));
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/remove/roles")
    public ResponseEntity<String> removeRoles(@RequestBody RoleRequest roleRequest) {
        AppUser appUser = accountService.removeRolesToUser(roleRequest.getUsername(), roleRequest.getRole());
        if (appUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.ok("Roles removed successfully");
        }
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/makeUserToroot")
    @PreAuthorize("hasRole('ROLES_ADMIN')")
    public ResponseEntity<String> makeUserToRoot(@RequestParam String username) {
        AppUser appUser = accountService.makeUserToRoots(username);
        if (appUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.ok("User updated successfully");
        }
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/home")
    ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello World");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/delete")
    ResponseEntity<String> deleteUser(@RequestParam String username) {
        accountService.deleteUser(username);
        return ResponseEntity.ok("User deleted successfully");
    }
}

@Data
@AllArgsConstructor
class CreateDomaineRequest {
        private  String email;
        private  String domaineName;
}

@Data
@AllArgsConstructor
class ChangePasswordRequest {
    private String email;
    private String password;
    private String comfirmePassword;
}

@Data
@AllArgsConstructor
class AuthRequest {
    private String email;
    private String password;
}

@Data
@AllArgsConstructor
class RoleRequest {
    private String role;
    private String username;
}

@Data
@AllArgsConstructor
class AuthResponse {
    private String jwt;
    private AppUser user;
}

@Data
@AllArgsConstructor
class UserProfileFrom{
    private String username;
    private UserProfile userProfile;
}

