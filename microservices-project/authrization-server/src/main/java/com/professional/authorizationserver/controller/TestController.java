package com.professional.authorizationserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

	@GetMapping("/print")
	public ResponseEntity<String> test(){
		return new ResponseEntity<String>("just testing multible routes" , HttpStatus.OK);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<String> test1(@PathVariable Long id){
		return new ResponseEntity<String>("just testing allowed origins for : " + id , HttpStatus.OK);
	}
}
