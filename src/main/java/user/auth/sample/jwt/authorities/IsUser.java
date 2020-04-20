package user.auth.sample.jwt.authorities;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(value = "hasAnyAuthority('ROLE_ADMIN', 'ROLE_COMMON_USER')") // or @PreAuthorize("hasAnyRole('ADMIN', 'COMMON_USER')")
public @interface IsUser {

}
