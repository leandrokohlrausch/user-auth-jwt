package user.auth.sample.jwt;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import user.auth.sample.jwt.documents.RoleDocument;
import user.auth.sample.jwt.enumerations.RolesEnum;
import user.auth.sample.jwt.repositories.RolesRepository;

@AllArgsConstructor
@Component
@Order(value = 1)
public class RolesDummyData implements CommandLineRunner {

    private final RolesRepository rolesRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----------- Starting EXECUTION DUMMY DATA - Roles");
        rolesRepository.deleteAll().thenMany(
                Flux.just(RolesEnum.ROLE_ADMIN.name(), RolesEnum.ROLE_COMMON_USER.name())
                        .map(authority -> new RoleDocument(authority))
                        .flatMap(rolesRepository::save)
        ).subscribe(System.out::println);
    }
}
