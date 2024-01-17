package com.professional.authorizationserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.professional.authorizationserver.model.AuthenticationUser;
import com.professional.authorizationserver.repository.AuthenticationRepository;

@Service
public class _UserDetailsService implements UserDetailsService {

	@Autowired
	private AuthenticationRepository authenticationUserRepository;
	
	@Override
	public AuthenticationUser loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthenticationUser user = authenticationUserRepository.findByUserLoginName(username);
		if(user==null) 
			throw new UsernameNotFoundException("user name not found !!");
		return user;
	}

}
