package user.auth.sample.jwt.configuration;

import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class CustomTokenAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String BEARER = "Bearer ";

    private CustomTokenProvider customTokenProvider;

    public CustomTokenAuthenticationConverter(CustomTokenProvider customTokenProvider) {
        this.customTokenProvider = customTokenProvider;
    }

    @Override
    public Mono<Authentication> convert(final ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .map(CustomSecurityUtils::getTokenFromRequest)
                .filter(Objects::nonNull)
                .filter(authValue -> authValue.length() > BEARER.length())
                .map(authValue -> authValue.substring(BEARER.length()))
                .filter(Strings::isNotBlank)
                .map(customTokenProvider::getAuthentication)
                .filter(Objects::nonNull);
    }
}
