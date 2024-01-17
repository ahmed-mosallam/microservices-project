package com.professional.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import com.professional.orderservice.dto.InventoryResponse;
import com.professional.orderservice.dto.OrderRequest;
import com.professional.orderservice.dto.ResponseObject;
import com.professional.orderservice.event.OrderPlacedEvent;
import com.professional.orderservice.model.Order;
import com.professional.orderservice.model.OrderLineItems;
import com.professional.orderservice.repository.OrderRepository;
import com.professional.orderservice.tools.CommonMethods;
import com.professional.orderservice.utils.ObjectMapperUtils;



@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate; 
	
	private static final String checkProductsInStockServiceUrl="api/inventory";
	
	private static final String inventoryServiceBaseUrl="http://inventory-service/";
	
	public void placeOrder(OrderRequest orderRequest) {
		
		List<String> skuCodes = orderRequest.getOrderLineItemsDtoList().stream()
				.map(orderLineItem-> orderLineItem.getSkuCode()).toList(); 
		  
		
		/// call inventory service to check if products skuCode is in stock or not to place the order
		InventoryResponse[] inventoryResponses= 
				((UriSpec<?>) webClientBuilder.build().get()
				                       .header("Authorization", request.getHeader("Authorization")))
	                                   .uri(inventoryServiceBaseUrl+checkProductsInStockServiceUrl
	                                		   , uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
	                                   .retrieve()
	                                   .bodyToMono(InventoryResponse[].class)	                                   
	                                   .block();
		
		Boolean allProductsInStock = Arrays.stream(inventoryResponses)
				                           .allMatch(inventoryResponse-> inventoryResponse.isInStock());
		
		/// checking if all skuCodes sent to inventory service is in stock 
		if(allProductsInStock) {		                              
			Order order =new Order();
			order.setOrderNumber(UUID.randomUUID().toString());
			List<OrderLineItems> orderLineItems = ObjectMapperUtils
					.mapAll(orderRequest.getOrderLineItemsDtoList(), OrderLineItems.class); 
			order.setOrderLineItemsList(orderLineItems);
			order.setUserId(CommonMethods.getAuthenticatedUser().getUserId());
			orderRepository.save(order);
			kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
		}else {
			throw new IllegalArgumentException("Product is not in stock, please try again later");
		}
		
	}
	
	public ResponseObject getUserOrders(Long userId){
		if(!CommonMethods.getAuthenticatedUser().getUserId().equals(userId))
			return ResponseObject.builder()
					.success(false).code(HttpStatus.UNAUTHORIZED.value())
					.message("you are not authorized to do this action")
					.status(HttpStatus.UNAUTHORIZED).build();
		return ResponseObject.builder()
				.success(true).code(HttpStatus.OK.value())
				.payload(orderRepository.findByUserId(userId)).status(HttpStatus.OK).build();
	}
}
