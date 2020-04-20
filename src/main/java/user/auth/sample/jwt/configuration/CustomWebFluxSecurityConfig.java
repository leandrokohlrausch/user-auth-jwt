package user.auth.sample.jwt.configuration;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import user.auth.sample.jwt.services.CustomUsersDetailsPasswordsService;
import user.auth.sample.jwt.services.CustomUsersDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class CustomWebFluxSecurityConfig {

    private final CustomUsersDetailsService customUsersDetailsService;

    private final CustomUsersDetailsPasswordsService customUsersDetailsPasswordsService;

    private final CustomTokenProvider customTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .exceptionHandling()
                .authenticationEntryPoint((exchange, e) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((exchange, e) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .authorizeExchange()
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(HttpMethod.POST, "/users/authenticate", "/users").permitAll()
                .and().addFilterAt(customWebFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .authorizeExchange()
                .anyExchange().authenticated()
                .and().build();
    }

    @Bean
    public AuthenticationWebFilter customWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(customReactiveAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(new CustomTokenAuthenticationConverter(customTokenProvider));
        authenticationWebFilter.setRequiresAuthenticationMatcher(new CustomJwtHeadersExchangeMatcher());
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return authenticationWebFilter;
    }

    @Bean
    public CustomReactiveAuthenticationManager customReactiveAuthenticationManager() {
        return new CustomReactiveAuthenticationManager(passwordEncoder(), customUsersDetailsService, customUsersDetailsPasswordsService);
    }
}
