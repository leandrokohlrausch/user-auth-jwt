package user.auth.sample.jwt;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import user.auth.sample.jwt.enumerations.RolesEnum;
import user.auth.sample.jwt.requests.UserRequest;
import user.auth.sample.jwt.services.AuthenticationsService;

import java.util.Arrays;

@AllArgsConstructor
@Component
@Order(value = 2)
public class UsersDummyData implements CommandLineRunner {

    private final AuthenticationsService authenticationsService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----------- Starting EXECUTION DUMMY DATA - Users ");
        authenticationsService.deleteAll().thenMany(
                Flux.just(new UserRequest("admin", "leandro", "Administrator", "admin@test.com",Arrays.asList(RolesEnum.ROLE_ADMIN.name())),
                        new UserRequest("user", "leandro", "Common User", "user@test.com", Arrays.asList(RolesEnum.ROLE_COMMON_USER.name()))
                ).flatMap(authenticationsService::create)
        ).subscribe(System.out::println);
    }
}
