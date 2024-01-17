package com.professional.productservice;




import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.anything;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.professional.productservice.dto.ProductRequest;
import com.professional.productservice.model.Product;
import com.professional.productservice.repository.ProductRepository;
import com.professional.productservice.service.ProductService;
import com.professional.productservice.utils.ObjectMapperUtils;

import net.bytebuddy.NamingStrategy.Suffixing.BaseNameResolver.ForGivenType;


@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
class ProductServiceApplicationTests {

	@Autowired
	private ProductRepository productRepository;	
	
	@Autowired
	private MockMvc mockMvc;
	
	 @Autowired
	 private ObjectMapper objectMapper;
	
//	@BeforeEach
//	void setUp() {
//		productService = new ProductService(productRepository);
//	}
	
//	@Test
//	@Disabled
//	void contextLoads() {
//		ProductRequest productRequest = new ProductRequest("product2","product num 3 " , BigDecimal.valueOf(400));
//		Product product = ObjectMapperUtils.map(productRequest, Product.class);
//		//given(productRepository.findIfNameExists(product.getName())).willReturn(true);
//		when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));
//		assertThatThrownBy(()-> productService.save(productRequest) )
//		.isInstanceOf(RuntimeException.class)
//		.hasMessageContaining("can't create product: "+ product.getName() + " as it is already existed");
//	
//		verify(productRepository, never()).save(product);
//		
//	}
	
	@Test
	@Disabled
	void testAddProductIntegration() throws Exception {
		ProductRequest productRequest = new ProductRequest("product3","product num 3 " , BigDecimal.valueOf(400));
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(productRequestString))
		        .andExpect(status().isCreated());
		assertThat(productRepository.findByName(productRequest.getName())
				.isPresent()).isEqualTo(true);

	}

}
