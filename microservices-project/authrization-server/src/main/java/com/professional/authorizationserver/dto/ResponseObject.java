package com.professional.authorizationserver.dto;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseObject {

	private String token;
	private Object payload;
	private Integer code;
	private String message;
	private Boolean success;
	private HttpStatus status;
}
