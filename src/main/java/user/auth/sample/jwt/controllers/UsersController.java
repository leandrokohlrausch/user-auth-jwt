package user.auth.sample.jwt.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.authorities.IsAdmin;
import user.auth.sample.jwt.authorities.IsUser;
import user.auth.sample.jwt.requests.AuthenticationRequest;
import user.auth.sample.jwt.requests.UserRequest;
import user.auth.sample.jwt.responses.AuthenticationResponse;
import user.auth.sample.jwt.responses.UserResponse;
import user.auth.sample.jwt.services.AuthenticationsService;
import user.auth.sample.jwt.services.CustomUsersDetailsService;

@AllArgsConstructor
@RestController
public class UsersController {

    private final CustomUsersDetailsService customUsersDetailsService;

    private final AuthenticationsService authenticationsService;

    @RequestMapping(value = "/users/authenticate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationsService.authenticate(authenticationRequest);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> create(@RequestBody UserRequest userRequest) {
        return authenticationsService.create(userRequest);
    }

    @IsAdmin
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Flux<UserResponse> findAll() {
        return customUsersDetailsService.findAll();
    }

    @IsUser
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponse> findById(@PathVariable String id) {
        return customUsersDetailsService.findById(id);
    }

    @IsAdmin
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return customUsersDetailsService.delete(id);
    }

    @IsUser
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<UserResponse> update(@PathVariable String id, @RequestBody UserRequest userRequest) {
        userRequest.setPassword(null);
        return customUsersDetailsService.update(id, userRequest);
    }
}
