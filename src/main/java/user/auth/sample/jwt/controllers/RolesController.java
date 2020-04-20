package user.auth.sample.jwt.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.authorities.IsAdmin;
import user.auth.sample.jwt.requests.RoleRequest;
import user.auth.sample.jwt.responses.RoleResponse;
import user.auth.sample.jwt.services.RolesService;

@AllArgsConstructor
@RestController
public class RolesController {

    private final RolesService rolesService;

    @IsAdmin
    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoleResponse> create(@RequestBody RoleRequest request) {
        return rolesService.create(request);
    }

    @IsAdmin
    @RequestMapping(value = "/roles/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return rolesService.delete(id);
    }
}
