package com.professional.apigateway.dto;

import lombok.Data;

@Data
public class AuthenticationUserDTO {

	private Long userId;
	private String userLoginName;
	private String userRole;
	
}
