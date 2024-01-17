package com.professional.productservice.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private TokenVerificationFilter tokenVerificationFilter;
	
	
	
	private static List<String> applicationAllowedOrigins;
	
	@Value("${application.allowed.origins}")
	public void setApplicationAllowedOrigins(List<String> origins) {
		applicationAllowedOrigins=origins;
	}	
	
    private static List<String> applicationAllowedHttpMethods;
	
	@Value("${application.allowed.methods}")
	public void setApplicationAllowedHttpMethods(List<String> httpMethods) {
		applicationAllowedHttpMethods = httpMethods;
	}

    private static final String[] antMatchers = {"/user/login",
		     "/user/verify-token"};
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	

    @Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	   http.cors().and().csrf().disable()                 
	              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	              .and()
	              .addFilter(new UsernamePasswordAuthenticationFilter())
	              .addFilterAfter(tokenVerificationFilter, UsernamePasswordAuthenticationFilter.class)
	              .authorizeRequests()                
	              .antMatchers(antMatchers).permitAll()
	              .anyRequest()
	              .authenticated();
	
	      
	
	  return http.build();
   }
    
    
	@Bean
	public CorsFilter corsFilter() {
		 final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		    final CorsConfiguration config = new CorsConfiguration();
		    config.setAllowCredentials(true);
		    config.setAllowedOrigins(applicationAllowedOrigins);
		    config.setAllowedHeaders(List.of("Origin", "Content-Type", "Accept"));
		    config.setAllowedMethods(applicationAllowedHttpMethods);
		    source.registerCorsConfiguration("/**", config);
		    return new CorsFilter(source);
	} 
  
  
}
