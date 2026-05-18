package restaurant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import restaurant.entity.User;
import restaurant.repository.UserRepository;
import restaurant.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private restaurant.service.CartService cartService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/vendor/**", "/uploads/**").permitAll()
                        .requestMatchers("/select-branch/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/", "/home","/menu", "/food-detail" ,"/reservation","/market","/blog","/contact","/sign-in","/sign-up").permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .formLogin(login -> login
                        .loginPage("/sign-in")
                        .loginProcessingUrl("/do-login")
                        .successHandler((request, response, authentication) -> {
                            String email = authentication.getName();
                            User user = userRepository.findByEmail(email).orElse(null);

                            if (user != null) {
                                cartService.clearCart(user.getUserId());

                                request.getSession().setAttribute("currentBranchId", 1);
                                request.getSession().setAttribute("loggedInUser", user);
                            }

                            response.sendRedirect("/home");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                String email = authentication.getName();
                                userRepository.findByEmail(email).ifPresent(user -> {
                                    cartService.clearCart(user.getUserId());
                                });
                            }
                        })
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}