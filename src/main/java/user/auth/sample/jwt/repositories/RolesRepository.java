package user.auth.sample.jwt.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.documents.RoleDocument;

public interface RolesRepository extends ReactiveMongoRepository<RoleDocument, String> {

    Mono<RoleDocument> findByAuthority(final String authority);

}
