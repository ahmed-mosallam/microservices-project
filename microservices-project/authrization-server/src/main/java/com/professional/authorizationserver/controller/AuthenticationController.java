package com.professional.authorizationserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.professional.authorizationserver.dto.ResponseObject;
import com.professional.authorizationserver.service.AuthenticationService;

@RestController
@RequestMapping("/user")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/login")
	public ResponseEntity<ResponseObject> doLogin(@RequestBody ObjectNode authenticationData){
	    ResponseObject response = authenticationService.doLogin(authenticationData);
	    return new ResponseEntity<ResponseObject>(response , HttpStatus.OK);
	}
	
	@GetMapping("/verify-token")	
	public ResponseEntity<ResponseObject> verifyToken(@RequestParam String token){
		ResponseObject response = authenticationService.verifyToken(token);
		return new ResponseEntity<>(response , response.getStatus());
	}
}
