package com.professional.authorizationserver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationUserDTO {

	private Long userId;
	private String userFullName;
	private String userRole;
	private Boolean isVerifiedToken;
	private String verificationFailReason;
	
}
