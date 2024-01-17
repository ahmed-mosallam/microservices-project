package com.professional.productservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.professional.productservice.dto.ProductRequest;
import com.professional.productservice.dto.ProductResponse;
import com.professional.productservice.service.ProductService;


@RestController
@RequestMapping("/api/product")
//@RequiredArgsConstructor
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private HttpServletRequest request;
	
	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public void save(@RequestBody ProductRequest productRequest) {
		productService.save(productRequest);
	}
	
	@GetMapping
	@ResponseStatus(value = HttpStatus.OK)
	public List<ProductResponse> getAll(){
		System.out.println("authorization : " + request.getHeader("Authorization"));
		return productService.getAll();
	}
	
}
