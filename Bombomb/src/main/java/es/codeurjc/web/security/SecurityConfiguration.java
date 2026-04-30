package es.codeurjc.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.codeurjc.web.security.jwt.JwtTokenProvider;
import es.codeurjc.web.security.jwt.JwtRequestFilter;
import es.codeurjc.web.security.jwt.UnauthorizedHandlerJwt;


@Configuration
@EnableWebSecurity

public class SecurityConfiguration {

    @Autowired
    public RepositoryUserDetailsService userDetailsService;

    @Autowired
	private JwtTokenProvider jwtTokenProvider;

    @Autowired
  	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
    }

    @Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.securityMatcher("/api/**")
				.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http
				.authorizeHttpRequests(authorize -> authorize
						// PRIVATE ENDPOINTS
						// Images
						//.requestMatchers(HttpMethod.PUT, "/api/images/*/media").hasRole("USER")
						//.requestMatchers(HttpMethod.DELETE, "/api/books/*/images/*").hasRole("USER")
						// Books
						//.requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("USER")
						//.requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("USER")
						//.requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
						// Shops
						//.requestMatchers(HttpMethod.PUT, "/api/shops/**").hasRole("ADMIN")
						//.requestMatchers(HttpMethod.PUT, "/api/shops/**").hasRole("ADMIN")
						//.requestMatchers(HttpMethod.DELETE, "/api/shops/**").hasRole("ADMIN")
						// PUBLIC ENDPOINTS
						.anyRequest().permitAll());

		// Disable Form login Authentication
		http.formLogin(formLogin -> formLogin.disable());

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf(csrf -> csrf.disable());
                
		// Disable Basic Authentication
		http.httpBasic(httpBasic -> httpBasic.disable());

		// Stateless session
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(new JwtRequestFilter(userDetailsService, jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http
			.authorizeHttpRequests(authorize -> authorize
					/// PUBLIC PAGES
                    
					.requestMatchers("/").permitAll()
                    .requestMatchers("/error/**").permitAll()
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/signin").permitAll()
                    .requestMatchers("/css/*").permitAll()
                    .requestMatchers("/images/**").permitAll()
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
                    .requestMatchers("/new_file").hasAnyRole("USER")
                    .requestMatchers("/upload_file").hasAnyRole("USER")

                   
                    .requestMatchers("/delete/*/profile").hasAnyRole("ADMIN")
                    .requestMatchers("/delete/*/chocolate").hasAnyRole("ADMIN")
                    .requestMatchers("/custom/**").hasAnyRole("ADMIN")
                    .requestMatchers("/userList/**").hasAnyRole("ADMIN")
                    .requestMatchers("/profile/*/image").hasAnyRole("ADMIN")
                    .requestMatchers("/create/chocolate").hasAnyRole("ADMIN")
                    .requestMatchers("/adminAddBox/*").hasAnyRole("ADMIN")
                    .requestMatchers("/box/*/delete").hasAnyRole("ADMIN")
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
