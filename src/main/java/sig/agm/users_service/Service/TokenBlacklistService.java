package sig.agm.users_service.Service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    private final Set<String> revokedTokens = new HashSet<>();

    // Méthode pour ajouter un token à la liste des révoqués
    public void revokeToken(String token) {
        revokedTokens.add(token);
    }

    // Méthode pour vérifier si un token est révoqué
    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }
}
