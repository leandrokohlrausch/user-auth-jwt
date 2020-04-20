package user.auth.sample.jwt.configuration;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import user.auth.sample.jwt.documents.RoleDocument;
import user.auth.sample.jwt.documents.UserDocument;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@NoArgsConstructor
public class CustomTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";

    @Value("${application.security.jjwt.secret}")
    private String secret;

    @Value("${application.security.jjwt.expiration}")
    private int expirationTime;

    public String createToken(final Authentication authentication) {
        final var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        final var createdDate = new Date();
        final var expirationDate = new Date(System.currentTimeMillis() + 1000 * 60 * expirationTime);

        return Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities)
                .setSubject(authentication.getName())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Authentication getAuthentication(final String token) {
        if (token == null || !validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }

        final var claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        final var authorities = Stream.of(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(authority -> new RoleDocument(authority))
                .collect(Collectors.toList());

        final var principal = new UserDocument(claims.getSubject(),"", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Boolean validateToken(final String authToken) {
        Boolean isValid = Boolean.FALSE;
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            isValid = Boolean.TRUE;
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return isValid;
    }
}
