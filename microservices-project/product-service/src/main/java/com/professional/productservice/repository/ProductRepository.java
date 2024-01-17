package com.professional.productservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.professional.productservice.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByName(String name);
	@Query(value="select case when count(p.name) > 0 then true else false end from products p where p.name=?1", nativeQuery = true)
	Boolean findIfNameExists(String name);
}
