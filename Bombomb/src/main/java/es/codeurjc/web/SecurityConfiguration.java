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
					/// PUBLIC PAGES
                    
					.requestMatchers("/").permitAll()
                    .requestMatchers("/error/**").permitAll()
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/signin").permitAll()
                    .requestMatchers("/css/*").permitAll()
                    .requestMatchers("/images/*").permitAll()
                    .requestMatchers("/chocolate/**").permitAll()
                    .requestMatchers("/products").permitAll()
                    .requestMatchers("/product/*/image").permitAll()
                    .requestMatchers("/product/*/details").permitAll()
                // PRIVATE PAGES
                    
                    .requestMatchers("/customBox").hasAnyRole("USER")
                    .requestMatchers("/profile/*").hasAnyRole("USER")
                    .requestMatchers("/profile").hasAnyRole("USER")
                    .requestMatchers("/editprofile").hasAnyRole("USER")
                    .requestMatchers("/delete/profile").hasAnyRole("USER")
                    .requestMatchers("/cart").hasAnyRole("USER")
                    .requestMatchers("/customBox").hasAnyRole("USER")
                    .requestMatchers("/addChocolate/*").hasAnyRole("USER")
                    .requestMatchers("/emptyCustom").hasAnyRole("USER")
                    .requestMatchers("/order/close-cart").hasAnyRole("USER")
                    .requestMatchers("/payment").hasAnyRole("USER")
                    .requestMatchers("/randomize").hasAnyRole("USER")
                    .requestMatchers("/product/*/add-to-cart").hasAnyRole("USER")
                    .requestMatchers("/success").hasAnyRole("USER")
                    .requestMatchers("/fail").hasAnyRole("USER")
                    .requestMatchers("/delete-from-cart/*/box").hasAnyRole("USER")
                    .requestMatchers("/custom/*/add-to-cart").hasAnyRole("USER")

                   
                    .requestMatchers("/delete/*/profile").hasAnyRole("ADMIN")
                    .requestMatchers("/delete/*/chocolate").hasAnyRole("ADMIN")
                    .requestMatchers("/custom/**").hasAnyRole("ADMIN")
                    .requestMatchers("/userList/**").hasAnyRole("ADMIN")
                    .requestMatchers("/profile/*/image").hasAnyRole("ADMIN")
                    .requestMatchers("/create/chocolate").hasAnyRole("ADMIN")
                    .requestMatchers("/adminAddBox/*").hasAnyRole("ADMIN")
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
