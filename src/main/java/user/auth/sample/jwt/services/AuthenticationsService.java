package user.auth.sample.jwt.services;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.configuration.CustomReactiveAuthenticationManager;
import user.auth.sample.jwt.configuration.CustomTokenProvider;
import user.auth.sample.jwt.documents.UserDocument;
import user.auth.sample.jwt.repositories.RolesRepository;
import user.auth.sample.jwt.repositories.UsersRepository;
import user.auth.sample.jwt.requests.AuthenticationRequest;
import user.auth.sample.jwt.requests.UserRequest;
import user.auth.sample.jwt.responses.AuthenticationResponse;
import user.auth.sample.jwt.responses.UserResponse;

@AllArgsConstructor
@Service
public class AuthenticationsService {

    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final CustomTokenProvider customTokenProvider;

    private final UsersRepository usersRepository;

    private final RolesRepository rolesRepository;

    public Mono<ResponseEntity<AuthenticationResponse>> authenticate(AuthenticationRequest authenticationRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        Mono<Authentication> authentication = customReactiveAuthenticationManager.authenticate(authenticationToken);
        ReactiveSecurityContextHolder.withAuthentication(authenticationToken);
        return authentication.map(auth -> {
            return ResponseEntity.ok(new AuthenticationResponse(customTokenProvider.createToken(auth)));
        });
    }

    public Mono<UserResponse> create(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        UserDocument userdoc = new UserDocument(userRequest);
        return Flux.fromIterable(userRequest.getAuthorities())
                .flatMap(authority -> rolesRepository.findByAuthority(authority))
                .collectList().flatMap(roles -> {
                    userdoc.setAuthorities(roles);
                    return usersRepository.save(userdoc).flatMap(userDocument -> {
                        return Mono.just(new UserResponse(userDocument));
                    });
                });
    }
    //For use on dummy data
    public Mono<Void> deleteAll() {
        return usersRepository.deleteAll();
    }

}
