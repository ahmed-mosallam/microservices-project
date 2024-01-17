package com.professional.apigateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseObject {

	private int code;
	private boolean success;
	private String message;
	
}
