package es.codeurjc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity

public class SecurityConfiguration {

    @Autowired
    public RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http
			.authorizeHttpRequests(authorize -> authorize
					// PRIVATE PAGES
                    .requestMatchers("/customBox").hasAnyRole("USER")
                    .requestMatchers("/profile").hasAnyRole("USER")
                    .requestMatchers("/userList").hasAnyRole("ADMIN")
                    .requestMatchers("/cart").hasAnyRole("USER")
					// PUBLIC PAGES
					.anyRequest().permitAll()
                )
			.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.failureUrl("/error/login")
					.defaultSuccessUrl("/profile")
                    .usernameParameter("email")
					.permitAll()
			)
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);
		
		

		return http.build();
    }

}
