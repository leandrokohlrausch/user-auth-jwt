package user.auth.sample.jwt.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.documents.RoleDocument;
import user.auth.sample.jwt.repositories.RolesRepository;
import user.auth.sample.jwt.requests.RoleRequest;
import user.auth.sample.jwt.responses.RoleResponse;

@AllArgsConstructor
@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    public Mono<RoleResponse> create(RoleRequest request) {
        return rolesRepository.save(new RoleDocument(request.getAuthority())).flatMap(roleDocument -> {
            return Mono.just(new RoleResponse(roleDocument));
        });
    }

    public Mono<Void> delete(String id) {
        return rolesRepository.findById(id).flatMap(roleDocument -> {
            return rolesRepository.delete(roleDocument);
        });
    }
}
