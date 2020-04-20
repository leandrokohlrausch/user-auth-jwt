package user.auth.sample.jwt.configuration;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@NoArgsConstructor
public class CustomJwtHeadersExchangeMatcher implements ServerWebExchangeMatcher {

    @Override
    public Mono<MatchResult> matches(final ServerWebExchange exchange) {
        final var request = Mono.just(exchange).map(ServerWebExchange::getRequest).cast(ServerHttpRequest.class);
        // Check for header "Authorization"
        return request.map(ServerHttpRequest::getHeaders)
                .filter(h -> h.containsKey(HttpHeaders.AUTHORIZATION))
                .flatMap(s -> MatchResult.match())
                .switchIfEmpty(MatchResult.notMatch());
    }
}
