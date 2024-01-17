package com.professional.orderservice.dto;

import java.util.Arrays;
import java.util.Collection;



import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;


@Data
public class AuthenticationUser implements UserDetails {
    
    private static final long serialVersionUID = 1L;	
	
	private Long userId;
	
	
	private String userLoginName;
	
	
	private String userPassword;
	
	
	private String userRole;
	
	
	private String userFullName;
	
	
	private String token;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return Arrays.asList(new SimpleGrantedAuthority("ROLE_"+getUserRole().toUpperCase()));		
	}

	@Override
	public String getUsername() {
		return getUserLoginName();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getPassword() {
		return getUserPassword();
	}
	
}
