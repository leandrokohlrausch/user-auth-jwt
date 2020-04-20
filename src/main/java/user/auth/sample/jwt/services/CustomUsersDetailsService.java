package user.auth.sample.jwt.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.repositories.UsersRepository;
import user.auth.sample.jwt.requests.UserRequest;
import user.auth.sample.jwt.responses.UserResponse;

import java.util.Objects;

@AllArgsConstructor
@Service
public class CustomUsersDetailsService implements ReactiveUserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return usersRepository.findByUsername(username)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with login : " + username)))
                .cast(UserDetails.class);
    }

    public Mono<UserResponse> findById(String id) {
        return usersRepository.findById(id).flatMap(userDocument -> {
            return Mono.just(new UserResponse(userDocument));
        });
    }

    public Flux<UserResponse> findAll() {
        return usersRepository.findAll().flatMap(userDocument -> {
            return Mono.just(new UserResponse(userDocument));
        });
    }

    public Mono<Void> delete(String id) {
        return usersRepository.findById(id).flatMap(userDocument -> {
            return usersRepository.delete(userDocument);
        });
    }

    public Mono<UserResponse> update(String id, UserRequest userRequest) {
        return usersRepository.findById(id).flatMap(userDocument -> {
            userDocument.setRequestProperties(userRequest);
            return usersRepository.save(userDocument).flatMap(userDocument1 -> {
                return Mono.just(new UserResponse(userDocument1));
            });
        });
    }
}
