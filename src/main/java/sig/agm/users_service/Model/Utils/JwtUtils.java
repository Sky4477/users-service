package sig.agm.users_service.Model.Utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sig.agm.users_service.Service.TokenBlacklistService;

import java.util.Date;
import java.util.function.Function;

@Component

public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    private static final int EXTENSION_DAYS = 4;
    private static final long EXTENSION_DURATION_MS = EXTENSION_DAYS * 24 * 60 * 60 * 1000L;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;



    public String generateJwtToken(UserDetails userDetails) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(algorithm);
    }

    public String getUserNameFromJwtToken(String token) {
        return getClaimFromToken(token, DecodedJWT::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, DecodedJWT::getExpiresAt);
    }

    public boolean validateJwtToken(String token, UserDetails userDetails) {
        final String username = getUserNameFromJwtToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !tokenBlacklistService.isTokenRevoked(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public <T> T getClaimFromToken(String token, Function<DecodedJWT, T> claimsResolver) {
        final DecodedJWT decodedJWT = getAllClaimsFromToken(token);
        return claimsResolver.apply(decodedJWT);
    }

    private DecodedJWT getAllClaimsFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.require(algorithm).build().verify(token);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid JWT Token: " + ex.getMessage());
        }
    }

    public String refreshJwtToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        DecodedJWT decodedJWT = getAllClaimsFromToken(token);
        return JWT.create()
                .withSubject(decodedJWT.getSubject())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXTENSION_DURATION_MS))
                .sign(algorithm);
    }
}
