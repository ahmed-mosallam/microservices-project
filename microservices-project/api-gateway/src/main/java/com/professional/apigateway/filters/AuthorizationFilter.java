package com.professional.apigateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.common.base.Strings;
import com.professional.apigateway.dto.AuthenticationUserDTO;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

	@Autowired
	private WebClient.Builder webClientBuilder;
	
	public AuthorizationFilter() {
		super(Config.class);
	}
	
	public static class Config{
		
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange , chain)->{
			String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
			if(Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer")) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				throw new RuntimeException("Authorization header is missing or invalid token type!");
			}
			String token = authorizationHeader.split(" ",2)[1];
			return webClientBuilder.build().get().uri("http://authorization-server/user/verify-token?token="+token)
			                .retrieve()
			                .bodyToMono(AuthenticationUserDTO.class)
			                .map(userDto->{
			                	return exchange;
			                })
			                .flatMap(chain::filter);
		};
	}
}
