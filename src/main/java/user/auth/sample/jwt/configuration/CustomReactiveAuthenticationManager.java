package user.auth.sample.jwt.configuration;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private PasswordEncoder passwordEncoder;

    private ReactiveUserDetailsService userDetailsService;

    private ReactiveUserDetailsPasswordService userDetailsPasswordService;

    private Scheduler scheduler = Schedulers.newParallel("password-encoder", Schedulers.DEFAULT_POOL_SIZE, true);


    public CustomReactiveAuthenticationManager(PasswordEncoder passwordEncoder, ReactiveUserDetailsService userDetailsService, ReactiveUserDetailsPasswordService userDetailsPasswordService) {
        super();
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
        this.setUserDetailsPasswordService(userDetailsPasswordService);
    }

    private UserDetailsChecker preAuthenticationChecks = user -> {
        if (!user.isAccountNonLocked()) {

            throw new LockedException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.locked",
                    "User account is locked"));
        }

        if (!user.isEnabled()) {

            throw new DisabledException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.disabled",
                    "User is disabled"));
        }

        if (!user.isAccountNonExpired()) {

            throw new AccountExpiredException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.expired",
                    "User account has expired"));
        }
    };

    private UserDetailsChecker postAuthenticationChecks = user -> {
        if (!user.isCredentialsNonExpired()) {

            throw new CredentialsExpiredException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                    "User credentials have expired"));
        }
    };

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        return checkPasswordIfNotAuthenticated(authentication)
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
    }

    private Mono<UserDetails> checkPasswordIfNotAuthenticated(final Authentication authentication) {
        final String username = authentication.getName();
        final var userDetailsMono = retrieveUser(username).doOnNext(this.preAuthenticationChecks::check);
        return authentication.isAuthenticated()
                ? userDetailsMono
                : withPassword(userDetailsMono, (String) authentication.getCredentials());
    }

    private Mono<UserDetails> withPassword(final Mono<UserDetails> userDetailsMono, final String presentedPassword) {
        return userDetailsMono
                .publishOn(this.scheduler)
                .filter(u -> this.passwordEncoder.matches(presentedPassword, u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .flatMap(u -> {
                    final var upgradeEncoding = this.userDetailsPasswordService != null
                            && this.passwordEncoder.upgradeEncoding(u.getPassword());
                    if (upgradeEncoding) {
                        final var newPassword = this.passwordEncoder.encode(presentedPassword);
                        return this.userDetailsPasswordService.updatePassword(u, newPassword);
                    }
                    return Mono.just(u);
                }).doOnNext(this.postAuthenticationChecks::check);
    }

    protected Mono<UserDetails> retrieveUser(final String username) {
        return this.userDetailsService.findByUsername(username);
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserDetailsPasswordService(ReactiveUserDetailsPasswordService userDetailsPasswordService) {
        this.userDetailsPasswordService = userDetailsPasswordService;
    }

    public void setUserDetailsService(ReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setPostAuthenticationChecks(final UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

}
