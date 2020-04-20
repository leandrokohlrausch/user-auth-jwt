package user.auth.sample.jwt.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.documents.UserDocument;

import java.security.Principal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomSecurityUtils {

    public static String getTokenFromRequest(final ServerWebExchange serverWebExchange) {
        final var token = serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return token == null || token.isBlank() ? Strings.EMPTY : token;
    }

    public static Mono<UserDocument> getUserFromPrincipal(final Principal principal) {
        return Mono.just(principal)
                .cast(UsernamePasswordAuthenticationToken.class)
                .map(UsernamePasswordAuthenticationToken::getPrincipal)
                .cast(UserDocument.class);
    }

    public static Mono<UserDocument> getUserFromRequest(final ServerWebExchange serverWebExchange) {
        return serverWebExchange.getPrincipal().flatMap(CustomSecurityUtils::getUserFromPrincipal);
    }

}
