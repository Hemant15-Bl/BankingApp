package com.java.main.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.java.main.services.AccountService;
import com.java.main.services.AccountServiceImpl;

@Configuration
@EnableWebSecurity
public class SpringConfig {
	
	@Autowired
	AccountServiceImpl accountService;
	
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.csrf(csrf -> csrf.disable())
			.authorizeRequests(authz -> authz
					.requestMatchers("/register").permitAll()
					.anyRequest().authenticated()
					)
					.formLogin(form -> form.loginPage("/login")
								.loginProcessingUrl("/login")
								.defaultSuccessUrl("/dashboard",true)
								.permitAll()
					)
					 .logout(logout -> logout
								.invalidateHttpSession(true)
								.clearAuthentication(true)
								.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
								.logoutSuccessUrl("/login?logout")
								.permitAll()
					)
					 .headers(header -> header
							 .frameOptions(frameOptions -> frameOptions.sameOrigin())
					);
		
		return http.build();
								 
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
	}
}
