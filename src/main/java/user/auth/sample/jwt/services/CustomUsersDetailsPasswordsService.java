package user.auth.sample.jwt.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import user.auth.sample.jwt.documents.UserDocument;
import user.auth.sample.jwt.repositories.UsersRepository;

@AllArgsConstructor
@Service
public class CustomUsersDetailsPasswordsService implements ReactiveUserDetailsPasswordService {

    private final UsersRepository usersRepository;

    @Override
    public Mono<UserDetails> updatePassword(final UserDetails user, final String newPassword) {
        final var userDocument = (UserDocument) user;
        userDocument.setPassword(newPassword);
        return usersRepository.save(userDocument).cast(UserDetails.class);
    }
}
