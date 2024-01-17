package com.professional.productservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.professional.productservice.dto.ProductRequest;
import com.professional.productservice.dto.ProductResponse;
import com.professional.productservice.model.Product;
import com.professional.productservice.repository.ProductRepository;
import com.professional.productservice.utils.ObjectMapperUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
//@RequiredArgsConstructor
@Slf4j
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	public void save(ProductRequest productRequest) {
		Product product = ObjectMapperUtils.map(productRequest, Product.class);
		Boolean isExistedProduct = productRepository.findByName(product.getName()).isPresent();
		if(isExistedProduct) {
			throw new RuntimeException("can't create product: "+ product.getName() + " as it is already existed");
		}
		 productRepository.save(product);
		//log.info("product no. :  {} has been saved", id);
	}
	
	public List<ProductResponse> getAll(){
		List<Product> products = productRepository.findAll();
		return ObjectMapperUtils.mapAll(products, ProductResponse.class);
	}
}
