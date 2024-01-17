package com.professional.orderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.professional.orderservice.dto.OrderRequest;
import com.professional.orderservice.dto.ResponseObject;
import com.professional.orderservice.service.OrderService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;


@RestController
@RequestMapping("/api/order")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@PostMapping("/create")
	@ResponseStatus(value = HttpStatus.CREATED)
	@CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
  //  @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
	public String placeOrder(@RequestBody OrderRequest orderRequest) {
		orderService.placeOrder(orderRequest);
		return "Order Placed Successfully";
	}
	
	@GetMapping("/user/{id}")
	public ResponseEntity<ResponseObject> getUserOrders(@PathVariable Long id){
		ResponseObject response = orderService.getUserOrders(id);
		return new ResponseEntity<ResponseObject>(response,response.getStatus());
	}
	
	public String fallbackMethod(OrderRequest orderRequest , RuntimeException runtimeException) {
		return "Oops! Something went wrong, please order after some time!";
	}
	
	
}
