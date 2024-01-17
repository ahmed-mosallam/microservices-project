package com.professional.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.professional.authorizationserver.model.AuthenticationUser;

public interface AuthenticationRepository extends JpaRepository<AuthenticationUser, Long> {

	AuthenticationUser findByUserLoginName(String username);
}
