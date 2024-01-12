package com.paymybuddy.app.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	private static final Logger logger = LogManager.getLogger(SpringSecurityConfig.class);

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		logger.info("Configuring security filter chain");

		return http.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/userAccount").hasAnyRole("USER");
			auth.requestMatchers("/transfer").hasAnyRole("USER");
			auth.requestMatchers("/contactList").hasAnyRole("USER");
			auth.requestMatchers("/reloadAccount").hasAnyRole("USER");
			auth.requestMatchers("/transferToBank").hasAnyRole("USER");
			auth.anyRequest().permitAll();
		}).formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/userAccount"))
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.invalidateHttpSession(true).clearAuthentication(true).logoutSuccessUrl("/login?logout"))
				.rememberMe(rememberMe -> rememberMe.key("uniqueAndSecret").tokenValiditySeconds(86400))
				.csrf(Customizer.withDefaults()).build();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder)
			throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(customUserDetailsService)
				.passwordEncoder(bCryptPasswordEncoder);
		return authenticationManagerBuilder.build();
	}
}