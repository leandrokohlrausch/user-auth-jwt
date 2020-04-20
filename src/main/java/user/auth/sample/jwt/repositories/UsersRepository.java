package user.auth.sample.jwt.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.documents.UserDocument;

public interface UsersRepository extends ReactiveMongoRepository<UserDocument, String> {

    Mono<UserDocument> findByUsername(final String username);

    Mono<UserDocument> findByEmail(final String email);

}
