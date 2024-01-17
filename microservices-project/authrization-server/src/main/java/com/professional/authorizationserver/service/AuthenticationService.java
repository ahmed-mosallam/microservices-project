package com.professional.authorizationserver.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.professional.authorizationserver.dto.AuthenticationUserDTO;
import com.professional.authorizationserver.dto.ResponseObject;
import com.professional.authorizationserver.enums.TokenVerificationFailReason;
import com.professional.authorizationserver.model.AuthenticationUser;
import com.professional.authorizationserver.repository.AuthenticationRepository;
import com.professional.authorizationserver.tools.CommonMethods;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

@Service
public class AuthenticationService {

	@Autowired
	private AuthenticationRepository authenticationUserRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;	
	
	
	
	
	
	public ResponseObject doLogin(ObjectNode authenticationData) {
		AuthenticationUser user = authenticationUserRepository.findByUserLoginName(authenticationData.get("username").asText());
		if(user!=null) {
			Authentication auth = new UsernamePasswordAuthenticationToken(authenticationData.get("username").asText()
					, authenticationData.get("password").asText());
			authenticationManager.authenticate(auth);			
			String token = CommonMethods.createToken(getTokenBodyData(user));
			user.setToken(token);
			CommonMethods.registerUserInSecurityContext(user);
			
			ResponseObject response = ResponseObject.builder()
					                                .code(HttpStatus.OK.value())
					                                .success(true)
					                                .status(HttpStatus.OK)
					                                .token(token)					                                
					                                .build();
			return response;
		}else
			throw new BadCredentialsException("wrong user name or password !!");
	}
	
	public ResponseObject verifyToken(String requestToken) {
		try {
		      Claims claims =  CommonMethods.getTokenClaims(requestToken);
		      AuthenticationUser user = CommonMethods.createAuthenticationUserFromTokenClaims(claims, requestToken);
		      AuthenticationUserDTO userDTO = AuthenticationUserDTO.builder()
                      .userId(user.getUserId())
                      .userFullName(user.getUserFullName())
                      .userRole(user.getUserRole())
                      .isVerifiedToken(true)
                      .build();
		      return ResponseObject.builder().success(true).code(HttpStatus.OK.value()).status(HttpStatus.OK).payload(userDTO).build();
		   }catch (ExpiredJwtException e) {
			  ResponseObject response = CommonMethods.createResponseObjectForTokenVerificationFailure(TokenVerificationFailReason.EXPIRED_TOKEN.name());
			  return response;
		    }
		    catch (Exception e) {
		    	ResponseObject response = CommonMethods.createResponseObjectForTokenVerificationFailure(TokenVerificationFailReason.UNTRUSTED_TOKEN.name());
				  return response;
		    }
	} 
	
	
	
	private ObjectNode getTokenBodyData(AuthenticationUser user) {
		ObjectMapper mapper = new ObjectMapper();
		String authorities = user.getUserRole().equals("admin")?"ROLE_ADMIN":"ROLE_CUSTOMER";
	    ObjectNode tokenBodyData = mapper.createObjectNode();
	    tokenBodyData.put("id", user.getUserId());
	    tokenBodyData.put("username", user.getUserLoginName());
	    tokenBodyData.put("type", user.getUserRole());
	    tokenBodyData.put("userFullName", user.getUserFullName());
	    tokenBodyData.put("authorities", authorities);
	    return tokenBodyData;
	}
}
