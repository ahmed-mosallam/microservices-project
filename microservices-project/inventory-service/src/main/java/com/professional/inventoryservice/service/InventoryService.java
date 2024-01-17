package com.professional.inventoryservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.professional.inventoryservice.dto.InventoryResponse;
import com.professional.inventoryservice.repository.InventoryRepository;


@Service
public class InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Transactional(readOnly = true)
	public List<InventoryResponse> isInStock(List<String> skuCode) {
		return inventoryRepository.findBySkuCodeIn(skuCode).stream().map((inventory)->
			InventoryResponse.builder()
			                 .skuCode(inventory.getSkuCode())
			                 .isInStock(inventory.getQuantity()>0).build()
		).toList();
	}
}
