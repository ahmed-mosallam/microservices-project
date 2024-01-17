package com.professional.productservice.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.professional.productservice.dto.AuthenticationUser;
import com.professional.productservice.dto.ResponseObject;
import com.professional.productservice.tools.CommonMethods;

import io.jsonwebtoken.Claims;

@Component
public class TokenVerificationFilter extends OncePerRequestFilter{
    
	private static String tokenPrefix;
	
	@Value("${jwt.prefix}")
	public void setTokentPrefix(String prefix) {
		tokenPrefix=prefix;
	}
	
	@Autowired
	private ObjectMapper mapper;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");
		if(Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(tokenPrefix))
		  filterChain.doFilter(request, response);
		else {
			try {
			  String token = authorizationHeader.split(" ", 2)[1];	
		      Claims claims =	CommonMethods.getTokenClaims(token);
		      AuthenticationUser user = CommonMethods.createAuthenticationUserFromTokenClaims(claims, token);
		      CommonMethods.registerUserInSecurityContext(user);
		      filterChain.doFilter(request, response);
			}catch (Exception e) {
				ResponseObject unAuthorizedUserResponse =
						CommonMethods.createResponseObjectForUnAuthorizedUser();
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				mapper.writeValue(response.getWriter(), unAuthorizedUserResponse);
			}
		    
		}
		
	}

}
