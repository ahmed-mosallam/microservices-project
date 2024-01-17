package com.professional.authorizationserver.tools;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.professional.authorizationserver.dto.ResponseObject;
import com.professional.authorizationserver.model.AuthenticationUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CommonMethods {
	
	
	private static final String secretKey="dfsldkfjlkd#$^kjlkhlhkgtgfg@@!!!!jkj125";
	
	private static int expirationPeriod;	
	
	private static String tokenPrefix;	
	
		
	@Value("${jwt.expiration.period}")
	public void setExpirationPeriod(int period) {
		expirationPeriod=period;
	}
	
	@Value("${jwt.prefix}")
	public void setTokentPrefix(String prefix) {
		tokenPrefix=prefix;
	}	

	
	
	private static ObjectMapper mapper;
	
	
	
	@Autowired
	public void setMapper(ObjectMapper objectMapper) {
		mapper=objectMapper;
	}
	
	public static String createToken(ObjectNode tokenBodyData) {
		System.out.println("secret key " + secretKey);
		System.out.println("expirationPeriod " + expirationPeriod);
		String token = Jwts.builder()
				           .addClaims(tokenClaims(tokenBodyData))
				           .setIssuedAt(new Date())
				           .setExpiration(new Date(System.currentTimeMillis()+(expirationPeriod*60*1000)))
				           .signWith(getTokenSecretKey(secretKey))
				           .compact();
		
		return tokenPrefix+" "+ token;
	}
	
	public static Map<String,Object> tokenClaims (ObjectNode tokenBodyData){
		Map<String,Object> claims = new HashMap<>();
		Iterator<String> keys= tokenBodyData.fieldNames();		
		while (keys.hasNext()) {
			String key = keys.next();
			claims.put(key,tokenBodyData.get(key));
		}
		System.out.println("token claims result " + claims);
		return claims;
	}
	
	public static Key getTokenSecretKey(String secretKey) {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
	
	public static void registerUserInSecurityContext(AuthenticationUser user) {
		Authentication authentication = new UsernamePasswordAuthenticationToken(user , null , user.getAuthorities());
		System.out.println("inside registerUserInSecurityContext " + authentication);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	
	public static Claims getTokenClaims(String token) {
		Jws<Claims> tokenClaims = Jwts.parserBuilder()
				             .setSigningKey(getTokenSecretKey(secretKey))
				             .build()
				             .parseClaimsJws(token);
		return tokenClaims.getBody();		             
	}
	
	public static AuthenticationUser createAuthenticationUserFromTokenClaims(Claims claims, String token) {
		AuthenticationUser user = new AuthenticationUser();
		user.setUserId(Long.valueOf(claims.get("id").toString()));
		user.setUserFullName(claims.get("userFullName").toString());
		user.setUserRole(claims.get("type").toString());
		user.setUserLoginName(claims.get("username").toString());
		user.setToken(token);
		return user;
	}
	
	public static ObjectNode getTokenBodyAsObjectNode(String token) throws JsonParseException, JsonMappingException, IOException {
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String[] splits = token.split("\\.");			
		ObjectNode node = mapper.readValue(decoder.decode(splits[1]), ObjectNode.class);
		return node;
	}
	
	
	public static ResponseObject createResponseObjectForNewToken(String newToken) {
		ResponseObject response = ResponseObject.builder()
				                  .success(true)
				                  .code(HttpStatus.UNAUTHORIZED.value())
				                  .token(newToken)
				                  .build();
		return response;
	}
	
	public static ResponseObject createResponseObjectForTokenVerificationFailure(String message) {
		ResponseObject response = ResponseObject.builder()
				                  .success(true)
				                  .code(HttpStatus.UNAUTHORIZED.value())
				                  .message(message)
				                  .status(HttpStatus.UNAUTHORIZED)
				                  .build();
		return response;
	}
	
	public static ResponseObject createResponseObjectForUnAuthorizedUser() {
		ResponseObject response = ResponseObject.builder()
		                .code(HttpStatus.FORBIDDEN.value())
		                .success(false)
		                .message("Unauthorized User")
		                .build();
		return response;
	}
	
}

