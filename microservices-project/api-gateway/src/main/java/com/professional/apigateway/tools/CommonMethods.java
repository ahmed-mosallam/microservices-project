package com.professional.apigateway.tools;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.professional.apigateway.dto.ResponseObject;



@Component
public class CommonMethods {

	public static ResponseObject createResponseObjectForUnAuthorizedUser() {
		ResponseObject response = ResponseObject.builder()
		                .code(HttpStatus.UNAUTHORIZED.value())
		                .success(false)
		                .message("Unauthorized User")
		                .build();
		return response;
	}
}
